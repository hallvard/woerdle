package no.hal.woerdle.dict;

/**
 * Enum of dictionary resources.
 */
public enum ResourceDicts {
  NB("Bokmål", "NSF2021.txt");

  private final String label;
  private final String resource;

  private ResourceDicts(String label, String resource) {
    this.label = label;
    this.resource = resource;
  }

  public String label() {
    return label;
  }

  public String resource() {
    return resource;
  }

  public Dict getDict() {
    return getDict(resource);
  }

  public static Dict getDict(String resource) {
    return new ResourceDict("/no/hal/woerdle/dict/" + resource);
  }
}
