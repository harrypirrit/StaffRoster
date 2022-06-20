import { Status } from "./status";
import { User } from "./user";

export class Event {
    constructor(
        public eventID: number,
        public userID: number,
        public date: string,
        public location: string,
        public duration: number,
        public description: string,
        public statusID: number,
        public status: Status,
        public assignees: User[],
        public creator: User,
    ) {}
}
