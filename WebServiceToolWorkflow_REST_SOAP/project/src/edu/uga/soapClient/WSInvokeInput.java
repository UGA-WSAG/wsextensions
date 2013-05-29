package edu.uga.soapClient;

public class WSInvokeInput
{
    private String name = null;
    private String type = null;
    private String value = null;
    private String description = null;
    private Boolean required = null;
    
    public WSInvokeInput() {
		
	} // default constructor
    
    public String getName()
    {
        return name;
    } // getName
    public void setName(String name)
    {
        this.name = name;
    } // setName
    public String getType()
    {
        return type;
    } // getType
    public void setType(String type)
    {
        this.type = type;
    } // setType
    public String getValue()
    {
        return value;
    } // getValue
    public void setValue(String value)
    {
        this.value = value;
    } // setValue
    public String getDescription()
    {
        return description;
    } // getDescription
    public void setDescription(String description)
    {
        this.description = description;
    } // setDescription
    public Boolean getRequired()
    {
        return required;
    } // getRequired
    public void setRequired(Boolean required)
    {
    	this.required = required;
    } // setRequired
}