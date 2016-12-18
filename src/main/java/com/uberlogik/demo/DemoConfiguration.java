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
    private DataSourceFactory database;

    public DataSourceFactory dataSourceFactory()
    {
        return database;
    }

    @JsonProperty
    @NotNull
    private JooqFactory jooq = new JooqFactory(); // Defaults are acceptable

    public JooqFactory jooq()
    {
        return jooq;
    }



}
