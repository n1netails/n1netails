import { Organization } from "./organization";

export class User {
    public id: number;
    public userId: string;
    public provider: string;
    public providerId: string;
    public firstName: string;
    public lastName: string;
    public username: string;
    public email: string;
    public emailVerified: boolean;
    public password: string;
    public lastLoginDate: Date;
    public lastLoginDateDisplay: Date;
    public joinDate: Date;
    public profileImageUrl: string;
    public active: boolean;
    public notLocked: boolean;
    public role: string;
    public authorities: string[];
    public organizations: Organization[];

    constructor() {
        this.id = 0;
        this.userId = '';
        this.provider = '';
        this.providerId = '';
        this.firstName = '';
        this.lastName = '';
        this.username = '';
        this.email = '';
        this.emailVerified = false;
        this.password = '';
        this.lastLoginDate = new Date();
        this.lastLoginDateDisplay = new Date();
        this.joinDate = new Date();
        this.profileImageUrl = '';
        this.active = false;
        this.notLocked = false;
        this.role = '';
        this.authorities = [];
        this.organizations = [];
    }
}
