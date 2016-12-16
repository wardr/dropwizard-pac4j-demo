package com.uberlogik.demo;

import com.bendb.dropwizard.jooq.JooqFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import org.pac4j.dropwizard.Pac4jFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class DemoConfiguration extends Configuration
{
    @JsonProperty
    @NotNull
    private JooqFactory jooq = new JooqFactory(); // Defaults are acceptable

    @JsonProperty
    @NotNull
    private DataSourceFactory database;

    public JooqFactory jooq()
    {
        return jooq;
    }

    public DataSourceFactory dataSourceFactory()
    {
        return database;
    }

    @NotNull
    Pac4jFactory pac4jFactory = new Pac4jFactory();

    @JsonProperty("pac4j")
    public Pac4jFactory getPac4jFactory()
    {
        return pac4jFactory;
    }

    @JsonProperty("pac4j")
    public void setPac4jFactory(Pac4jFactory pac4jFactory)
    {
        this.pac4jFactory = pac4jFactory;
    }
}
