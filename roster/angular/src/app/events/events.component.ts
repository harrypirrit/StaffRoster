import {Component, OnInit} from '@angular/core';
import {CalendarEvent, CalendarEventAction, CalendarView} from 'angular-calendar';
import {Subject} from 'rxjs/internal/Subject';
import {EventService} from '../services/event.service';
import {Event} from '../models/event';
import isSameMonth from 'date-fns/isSameMonth';
import isSameDay from 'date-fns/isSameDay';
import {Status} from '../models/status';
import {User} from '../models/user';
import {LoginService} from '../services/login.service';
import {ActivatedRoute, Params, Router} from '@angular/router';

@Component({
  templateUrl: './events.component.html'
})
export class EventsComponent implements OnInit {

  constructor(private _eventService: EventService, private _loginService: LoginService, private _router: Router, private _route: ActivatedRoute) {
  }

  public view: CalendarView = CalendarView.Week;

  public calendarView = CalendarView;

  public viewDate: Date = new Date();

  public refresh = new Subject<void>();

  public events: CalendarEvent[] = [];

  public activeDayIsOpen: boolean = false;

  private _events: Event[] | undefined;

  public refreshing: boolean = false;

  public viewing: number | string | undefined;

  public adding: boolean = false;

  public actions: CalendarEventAction[] = [
    {
      label: 'View',
      a11yLabel: 'View',
      onClick: ({event}: { event: CalendarEvent }): void => {
        this.openView(event);
      },
    }
  ];

  // Add colours to each status
  public statusColours: any = [
    {
      id: 1, // Planned
      primary: '#1e90ff',
      secondary: '#D1E8FF',
    },
    {
      id: 2, // Complete
      primary: '#39E80C',
      secondary: '#A2F497'
    },
    {
      id: 3, // Cancelled
      primary: '#ad2121',
      secondary: '#FAE3E3',
    },
    {
      id: 4, // Postponed
      primary: '#e3bc08',
      secondary: '#FDF1BA',
    }
  ];

  public async ngOnInit() {
    this._route.queryParams.subscribe(async queryParams => {
      let addEvent = queryParams['addEvent'];
      let viewEvent = queryParams['viewEvent'];
      if (addEvent || viewEvent) {
        if (addEvent) {
          this.adding = true;
        } else if (viewEvent) {
          await this.getEvents();
          this.viewing = viewEvent;
        }
        const qParams: Params = {};
        this._router.navigate([], {
          relativeTo: this._route,
          queryParams: qParams,
          queryParamsHandling: ''
        });
      }
    });

    await this.getEvents();
  }

  public async getEvents() {
    this.events = [];
    // Get all events (filters by user in backend)
    this._events = await this._eventService.getEvents().toPromise();
    if (this._events) {
      // Sort through events and build calendar event object
      for (let i = 0; i < this._events.length; i++) {
        const date = new Date(this._events[i].date);
        const status = this._events[i].status;
        let newEvent: CalendarEvent = {
          id: this._events[i].eventID,
          start: date,
          title: this._events[i].description,
          color: this.statusColours.find((x: { id: number; }) => x.id == status.statusID)

        };
        const endDate = new Date(date);
        endDate.setHours(endDate.getHours() + this._events[i].duration);
        newEvent.end = endDate;
        this.events.push(newEvent);
      }
    }
    this.refresh.next();
  }

  setView(view: CalendarView) {
    this.view = view;
  }

  closeOpenMonthViewDay() {
    this.activeDayIsOpen = false;
  }

  public openView(event: CalendarEvent): void {
    this.viewing = event.id;
  }

  public closeView() {
    this.viewing = undefined;
  }

  public toggleAdd() {
    this.adding = !this.adding;
    if (!this.adding) {
      console.log("Refreshing...");
      this.getEvents();
    }
  }

  public get getEvent(): Event {
    let tmp = this._events?.find(x => x.eventID == this.viewing);
    return tmp ? tmp : new Event(0, 0, "", "", 0, "", 0, new Status(0, ""), [], new User(0, "", "", "", null, "", 0, "", false));
  }

  // When in month view open a list of the selected day's events
  dayClicked({date, events}: { date: Date; events: CalendarEvent[] }): void {
    if (isSameMonth(date, this.viewDate)) {
      if ((isSameDay(this.viewDate, date) && this.activeDayIsOpen) || events.length === 0 ) {
        this.activeDayIsOpen = false;
      } else {
        this.activeDayIsOpen = true;
      }
      this.viewDate = date;
    }
  }

  public async deleteEvent() {
    await this._eventService.deleteEvent(this.viewing).toPromise();
    this.closeView();
    this.getEvents();
  }
}
