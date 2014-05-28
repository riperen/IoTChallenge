/* Copyright (c) 2014, AMIS. All rights reserved. */

package bean;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import adapter.OccupancyAdapter;

/**
 * A JAX-RS application for Jersey integration
 */
@ApplicationPath("/")
public class WebApplication extends Application
{

  @Override
  public Set<Class<?>> getClasses()
  {
    final Set<Class<?>> classes = new HashSet<Class<?>>();
    // register root resource
    classes.add(OccupancyAdapter.class);
    classes.add(OccupancyListener.class);
    //Add here the resource classes which need to exposed via Jersey
    return classes;
  }
}

