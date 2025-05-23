import React, { useEffect, useState } from 'react';
import type {Person} from '../models/Person.ts'
import type { Gym } from '../models/Gym';
import apiClient from '../api/apiClient';
import { Box, Button, TextField, Typography, Select, MenuItem, List, ListItem, ListItemText } from '@mui/material';

const PeopleManager: React.FC = () => {
    const [people, setPeople] = useState<Person[]>([]);
    const [gyms, setGyms] = useState<Gym[]>([]);
    const [newPerson, setNewPerson] = useState<Person>({ name: '', phoneNumber: '', gymId: undefined });

    const fetchData = async () => {
        const [peopleRes, gymsRes] = await Promise.all([
            apiClient.get<Person[]>('/people'),
            apiClient.get<Gym[]>('/gyms')
        ]);
        setPeople(peopleRes.data);
        setGyms(gymsRes.data);
    };

    useEffect(() => {
        fetchData();
    }, []);

    const handleAddPerson = async () => {
        await apiClient.post('/people', newPerson);
        setNewPerson({ name: '', phoneNumber: '', gymId: undefined });
        fetchData();
    };

    const handleDeletePerson = async (id?: number) => {
        if (!id) return;
        await apiClient.delete(`/people/${id}`);
        fetchData();
    };

    return (
        <Box p={2}>
            <Typography variant="h5">Клиенты</Typography>
            <Box display="flex" gap={2} mt={2}>
                <TextField label="Имя" value={newPerson.name} onChange={e => setNewPerson({ ...newPerson, name: e.target.value })} />
                <TextField label="Телефон" value={newPerson.phoneNumber} onChange={e => setNewPerson({ ...newPerson, phoneNumber: e.target.value })} />
                <Select
                    value={newPerson.gymId || ''}
                    displayEmpty
                    onChange={e => setNewPerson({ ...newPerson, gymId: Number(e.target.value) })}
                >
                    <MenuItem value="">Выберите зал</MenuItem>
                    {gyms.map(gym => (
                        <MenuItem key={gym.id} value={gym.id}>{gym.type}</MenuItem>
                    ))}
                </Select>
                <Button variant="contained" onClick={handleAddPerson}>Добавить</Button>
            </Box>
            <List>
                {people.map(person => (
                    <ListItem key={person.id} secondaryAction={
                        <Button onClick={() => handleDeletePerson(person.id)}>Удалить</Button>
                    }>
                        <ListItemText
                            primary={`${person.name} (${person.phoneNumber})`}
                            secondary={`Зал: ${person.gym?.type || 'не назначен'}`}
                        />
                    </ListItem>
                ))}
            </List>
        </Box>
    );
};

export default PeopleManager;