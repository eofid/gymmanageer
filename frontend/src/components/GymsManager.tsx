import React, { useEffect, useState } from 'react';
import type { Gym } from '../models/Gym';
import apiClient from '../api/apiClient';
import { Box, Button, TextField, Typography, List, ListItem, ListItemText } from '@mui/material';

const GymsManager: React.FC = () => {
    const [gyms, setGyms] = useState<Gym[]>([]);
    const [newGym, setNewGym] = useState<Gym>({ type: '', address: '', number: '' });

    const fetchGyms = async () => {
        const res = await apiClient.get<Gym[]>('/gyms');
        setGyms(res.data);
    };

    useEffect(() => {
        fetchGyms();
    }, []);

    const handleAddGym = async () => {
        await apiClient.post('/gyms', newGym);
        setNewGym({ type: '', address: '', number: '' });
        fetchGyms();
    };

    const handleDeleteGym = async (id?: number) => {
        if (!id) return;
        await apiClient.delete(`/gyms/${id}`);
        fetchGyms();
    };

    return (
        <Box p={2}>
            <Typography variant="h5">Залы</Typography>
            <Box display="flex" gap={2} mt={2}>
                <TextField label="Тип" value={newGym.type} onChange={e => setNewGym({ ...newGym, type: e.target.value })} />
                <TextField label="Номер" value={newGym.number} onChange={e => setNewGym({ ...newGym, number: e.target.value })} />
                <TextField label="Адрес" value={newGym.address} onChange={e => setNewGym({ ...newGym, address: e.target.value })} />
                <Button variant="contained" onClick={handleAddGym}>Добавить</Button>
            </Box>
            <List>
                {gyms.map(gym => (
                    <ListItem key={gym.id} secondaryAction={
                        <Button onClick={() => handleDeleteGym(gym.id)}>Удалить</Button>
                    }>
                        <ListItemText primary={`${gym.type} — ${gym.number} — ${gym.address}`} />
                    </ListItem>
                ))}
            </List>
        </Box>
    );
};

export default GymsManager;