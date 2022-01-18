package no.hal.woerdle.dict;

import java.io.InputStream;

/**
 * Dict that loads words from a resource.
 */
public class ResourceDict extends InputStreamDict {

  private final String resource;

  public ResourceDict(String resource) {
    this.resource = resource;
  }

  @Override
  public InputStream getInputStream() {
    return this.getClass().getResourceAsStream(resource);
  }

  public static Dict getResourceDict(String resource) {
    return new ResourceDict(resource);
  }
}
