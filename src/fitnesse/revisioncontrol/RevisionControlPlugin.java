package fitnesse.revisioncontrol;

import fitnesse.responders.ResponderFactory;
import static fitnesse.revisioncontrol.RevisionControlOperation.*;
import fitnesse.revisioncontrol.responders.*;
import fitnesse.revisioncontrol.widgets.DeletedPageWidget;
import fitnesse.wikitext.WidgetBuilder;

public class RevisionControlPlugin {
  public static void registerResponders(ResponderFactory responderFactory) {
    responderFactory.addResponder(ADD.getQuery(), AddResponder.class);
    responderFactory.addResponder(SYNC.getQuery(), SyncResponder.class);
    responderFactory.addResponder(CHECKOUT.getQuery(), CheckoutResponder.class);
    responderFactory.addResponder(CHECKIN.getQuery(), CheckinResponder.class);
    responderFactory.addResponder(DELETE.getQuery(), DeleteResponder.class);
    responderFactory.addResponder(REVERT.getQuery(), RevertResponder.class);
    responderFactory.addResponder(UPDATE.getQuery(), UpdateResponder.class);
    responderFactory.addResponder(STATUS.getQuery(), StatusResponder.class);
  }

  public static void registerWikiWidgets(WidgetBuilder widgetBuilder) {
    widgetBuilder.addWidgetClass(DeletedPageWidget.class);
  }
}
