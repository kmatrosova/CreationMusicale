class Ligne
{
  private long tick;
  private int key;
  private int velocity;

  public Ligne(long tick, int key, int velocity)
  {
    this.tick = tick;
    this.key = key;
    this.velocity = velocity;
  }
  public long getTick()
  {
    return this.tick;
  }
  public int getKey()
  {
    return this.key;
  }
  public int getVelocity()
  {
    return this.velocity;
  }
}

