package edu.uga.RESTClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.jdom.JDOMException;

/**
 * This class takes in a REST url with the user inputs and invokes the web
 * service it returns the received response from the service as an xml or binary
 * file
 *
 * @author Singaram
 *
 */
public class RestInvocation {

    private HttpURLConnection conn = null;
    private String contentType = null;
    private URL url = null;
    private String fileName = "temp.xml";
    private String result = null;
    private String xmlResult = null;
    private InputStream inputStream;

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public RestInvocation(URL passedUrl) {
        this.url = passedUrl;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getXmlResult() {
        return xmlResult;
    }

    public void setXmlResult(String xmlResult) {
        this.xmlResult = xmlResult;
    }

    /**
     * Invokes the REST service with given url and reads the stream sent from
     * the web service This method is called after setting the required
     * parameters like the url and xml file name
     *
     * @param method
     * @return String
     * @throws IOException
     * @throws IOException
     * @throws IOException
     */
    public RestInvokeResult invoke(String method) throws IOException {
        conn = (HttpURLConnection) url.openConnection();
        conn.addRequestProperty("User-Agent", "Mozilla/4.76");
        conn.setRequestProperty("Cookie", "foo=bar");
        if (method.equals("get")) {
            conn.setRequestMethod("GET");
        } else if (method.equals("post")) {
            conn.setRequestMethod("POST");
        } else if (method.equals("put")) {
            conn.setRequestMethod("PUT");
        } else if (method.equals("delete")) {
            conn.setRequestMethod("DELETE");
        }
        contentType = conn.getContentType();
        RestInvokeResult result = new RestInvokeResult();
        try {
            result.setHttpCode(conn.getResponseCode());
        } catch (IOException e1) {
            //when connection is refused!
            result.setResult("");
            result.setXmlResult(e1.toString());
            return result;
        }
        //System.out.println("Format : " + contentType);

        if (contentType == null) {
            if (conn.getResponseCode() != 200) {
                result.setInputStream(conn.getErrorStream());
            } else {
                result.setInputStream(conn.getInputStream());
            }
            result.setResult("Result type is unknown. Please try it again with the button above!");
            result.setXmlResult("Result type is unknown. Please try it again with the button above!");
        } else if (contentType.contains("xml")) {
            BufferedReader reader = null;
            if (conn.getResponseCode() != 200) {
                reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            } else {
                //for xml documents
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            }
            CreateXML(url.toString(), fileName, reader);
            result.setXmlResult(this.getXmlResult().replace("<", "&lt"));
            result.setXmlResult(this.getXmlResult().replace(">", "&gt"));
            //Parse the xml file
            XMLParser parse = new XMLParser();
            try {
                result.setResult(parse.parseXml(fileName));
            } catch (JDOMException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (contentType.contains("text")) {
            BufferedReader reader = null;
            if (conn.getResponseCode() != 200) {
                reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            } else {
                //for xml documents
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            }
            String next = null;
            result.setResult("");
            result.setXmlResult("");
            try {
                while ((next = reader.readLine()) != null) {
                    result.setXmlResult(result.getXmlResult().concat(next + "\n"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (conn.getResponseCode() != 200) {
                result.setInputStream(conn.getErrorStream());
            } else {
                result.setInputStream(conn.getInputStream());
            }
            //for other types
            //File file = new File("temp");
            //OutputStream out=new FileOutputStream(fileName);
//            result.setInputStream(conn.getInputStream());
//            byte buf[]=new byte[1024];
//            int len;
//            while((len=inputStream.read(buf))>0)
//            {
//                out.write(buf,0,len);
//            }
//            out.close();
            //inputStream.close();
            result.setResult("Result type not displayable but available as file. Please try it again with the link above!");
            result.setXmlResult("Result type not displayable but available as file. Please try it again with the link above!");
        }
        return result;
    }

    /**
     * creates an xml file for a given input stream
     *
     * @param url
     * @param fileName
     * @param reader
     * @throws IOException
     */
    public void CreateXML(String url, String fileName, BufferedReader reader) throws IOException {
        String next = null;
        File file = new File(fileName);
        FileWriter fileWriter = new FileWriter(file);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        this.setXmlResult("");
        try {
            while ((next = reader.readLine()) != null) {
                this.setXmlResult(this.getXmlResult().concat(next + "\n"));
                printWriter.println(next);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        printWriter.close();
        fileWriter.close();
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public HttpURLConnection getConn() {
        return conn;
    }

    public void setConn(HttpURLConnection conn) {
        this.conn = conn;
    }

    public static void main(String[] args) {
        try {
            RestInvocation restClient = new RestInvocation(new URL("http://mango.ctegd.uga.edu/jkissingLab/SWS/ToxodbAnnotatedWebservices/SearchForGenes/TextIDsOrganism/GeneIds/GeneByLocusTag.wadl"));
            restClient.invoke("post");
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
