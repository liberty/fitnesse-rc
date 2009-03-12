package fitnesse.revisioncontrol;

public class StatusResults extends Results {
  private boolean alertsFound;

  public void setAlertsFound(boolean alertsFound) {
    this.alertsFound = alertsFound;
  }

  public boolean isAlertsFound() {
    return alertsFound;
  }
}
