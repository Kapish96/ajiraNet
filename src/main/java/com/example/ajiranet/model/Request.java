package com.example.ajiranet.model;

import java.util.List;

public class Request
{
    private String name;

    private String source;

    private String type;

    private List<String>  targets;

    private String value;

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getSource ()
    {
        return source;
    }

    public void setSource (String source)
    {
        this.source = source;
    }

    public String getType ()
    {
        return type;
    }

    public void setType (String type)
    {
        this.type = type;
    }

    public List<String>  getTargets ()
    {
        return targets;
    }

    public void setTargets (List<String> targets)
    {
        this.targets = targets;
    }

    public String getValue ()
    {
        return value;
    }

    public void setValue (String value)
    {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [name = "+name+", source = "+source+", type = "+type+", targets = "+targets+", value = "+value+"]";
    }
}
