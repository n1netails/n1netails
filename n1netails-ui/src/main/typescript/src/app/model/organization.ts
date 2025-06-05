export class Organization {
    public id: number;
    public address: string;
    public description: string;
    public name: string;
    public createdAt: string;
    public updatedAt: string;

    constructor() {
        this.id = 0;
        this.address = '';
        this.description = '';
        this.name = '';
        this.createdAt = '';
        this.updatedAt = '';
    }
}