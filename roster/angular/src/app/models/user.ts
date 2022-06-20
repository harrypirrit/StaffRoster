export class User {
    constructor(
        public userID: number,
        public firstName: string,
        public lastName: string,
        public email: string,
        public password: string | null,
        public phoneNumber: string,
        public roleID: number,
        public roleName: string,
        public isAccepted: boolean
    ) {}
    
}
