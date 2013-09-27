package org.benjp.portlet.chat;

import org.exoplatform.calendar.service.CalendarEvent;
import org.exoplatform.calendar.service.GroupCalendarData;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.OrganizationService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@Named("calendarService")
@ApplicationScoped
public class CalendarService {

  org.exoplatform.calendar.service.CalendarService calendarService_;
  OrganizationService organizationService_;
  Logger log = Logger.getLogger("CalendarService");

  @Inject
  public CalendarService(org.exoplatform.calendar.service.CalendarService calendarService, OrganizationService organizationService)
  {
    calendarService_ = calendarService;
    organizationService_ = organizationService;
  }

/*
  private void saveEvent(String username, boolean isUserEvent, String calId, String summary,
                         int day, int fromHour, int fromMin, int toHour, int toMin) throws Exception
  {
    CalendarEvent event = new CalendarEvent();
    event.setCalendarId(calId);
    event.setSummary(summary);
    event.setEventType(CalendarEvent.TYPE_EVENT);
    event.setRepeatType(CalendarEvent.RP_NOREPEAT);
    event.setPrivate(isUserEvent);
    java.util.Calendar calendar = java.util.Calendar.getInstance();
    calendar.setTimeInMillis(calendar.getTime().getTime());
    calendar.set(java.util.Calendar.DAY_OF_WEEK, day);
    calendar.set(java.util.Calendar.HOUR_OF_DAY, fromHour);
    calendar.set(java.util.Calendar.MINUTE, fromMin);
    event.setFromDateTime(calendar.getTime());
    calendar.set(java.util.Calendar.HOUR_OF_DAY, toHour);
    calendar.set(java.util.Calendar.MINUTE, toMin);
    event.setToDateTime(calendar.getTime());
    if (isUserEvent)
      calendarService_.saveUserEvent(username, calId, event, true);
    else
      calendarService_.savePublicEvent(calId, event, true);
  }
*/

  protected void saveEvent(String user, String calName, String summary,
                         Date from, Date to) throws Exception
  {
    String calId = getCalendarId(user, calName);
    if (calId!=null) {
      CalendarEvent event = new CalendarEvent();
      event.setCalendarId(calId);
      event.setSummary(summary);
      event.setEventType(CalendarEvent.TYPE_TASK);
      event.setRepeatType(CalendarEvent.RP_NOREPEAT);
      event.setPrivate(true);
      event.setFromDateTime(from);
      event.setToDateTime(to);
      event.setPriority(CalendarEvent.PRIORITY_NORMAL);
      calendarService_.savePublicEvent(calId, event, true);
    }
  }

  protected void saveTask(String username, String summary,
                         Date from, Date to) throws Exception
  {
    String calId = getFirstCalendarsId(username);
    if (calId!=null) {
      CalendarEvent task = new CalendarEvent();
      task.setCalendarId(calId);
      task.setSummary(summary);
      task.setEventType(CalendarEvent.TYPE_TASK);
      task.setRepeatType(CalendarEvent.RP_NOREPEAT);
      task.setPrivate(true);
      task.setFromDateTime(from);
      task.setToDateTime(to);
      task.setPriority(CalendarEvent.PRIORITY_NORMAL);
      calendarService_.saveUserEvent(username, calId, task, true);
    }
  }

  private String getFirstCalendarsId(String username) {


    StringBuilder sb = new StringBuilder();
    List<org.exoplatform.calendar.service.Calendar> listUserCalendar = null;
    try {
      listUserCalendar = calendarService_.getUserCalendars(username, true);
      if (listUserCalendar.size()>0) {
        return listUserCalendar.get(0).getId();
      }
    } catch (Exception e) {
      log.info("Error while checking User Calendar :" + e.getMessage());
    }
    return null;
  }

  private String getCalendarId(String username, String space) {

    String id = null;
    StringBuilder sb = new StringBuilder();
    List<GroupCalendarData> listgroupCalendar = null;
    try {
      listgroupCalendar = calendarService_.getGroupCalendars(getUserGroups(username), true, username);
    } catch (Exception e) {
      log.info("Error while checking User Calendar :" + e.getMessage());
    }
    for (GroupCalendarData g : listgroupCalendar) {
      for (org.exoplatform.calendar.service.Calendar c : g.getCalendars()) {
        if (space.equals(c.getName())) {
          id = c.getId();
        }
      }
    }
    return id;
  }

  private String[] getUserGroups(String username) throws Exception {

    Object[] objs = organizationService_.getGroupHandler().findGroupsOfUser(username).toArray();
    String[] groups = new String[objs.length];
    for (int i = 0; i < objs.length; i++) {
      groups[i] = ((Group) objs[i]).getId();
    }
    return groups;
  }


}