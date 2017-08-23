Some notes for myself...

# Http.get() service
For example, we try to add a service which provides a list of people.

## Files
+ assets/data/people.json
+ app/app.module.ts
+ app/app.component.ts
+ app/services/person.ts
+ app/services/people.service.ts

## person.ts
```typescript
export class Person {
  id: number;
  name: string;
}
```

## people.service.ts
The following structure can be autocompleted by using `a-service (http)` instruction of **Angular v4 TypeScript Snippets**.
```typescript
import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Observable } from 'rxjs';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';

import { Person } from './person';

@Injectable()
export class PeopleService {
  url="./assets/data/people.json"; // the root directory of http.get() is always src
  constructor(private http: Http) { }

  getData(): Observable<Person[]> {
    this.http.get(this.url)
    .map(res => res.json())
    .catch(err => err.message || err);
  }
}
```

## app.module.ts
```typescript
import { HttpModule } from '@angular/http';

import { PeopleService } from './services/people.service';

imports: [ HttpModule ],
providers: [ PeopleService ]
```

## app.component.ts
```typescript
import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';

import { Person } from './services/person';
import { PeopleService } from './services/people.service';

@Component({
  template: `
    <ul>
      <li *ngFor="let person in people">
        {{ person.id }}: {{ person.name }}
      </li>
    </ul>
  `
})
export class AppComponent implements OnInit {
  people: Person[];
  errMsg: string;
  constructor(private pplSvc: PeopleService) { }
  ngOnInit(): void {
    this.pplSvc.getData().subscribe(
      ppl => this.people = ppl,
      err => this.errMsg = <any>err
    );
  }
}
```

## Knowledges
+ Observable vs. Promise
+ async vs. subscribe
+ the four ways (`Observable | async`, `Observable.subscribe`, `Promise | async`, `Promise.then`) can all do the work.
