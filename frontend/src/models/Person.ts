import type { Gym } from './Gym';

export interface Person {
    id?: number;
    name: string;
    phoneNumber?: string;
    gym?: Gym | null;
    gymId?: number | null;
}
