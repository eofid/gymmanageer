// src/pages/HomePage.tsx
import { useEffect, useState } from 'react';
import axios from 'axios';
import GymCard from '../components/GymCard';
import {
    Box,
    Typography,
    Button,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    TextField
} from '@mui/material';

interface Gym {
    id: number;
    type: string;
    number: string;
    address: string;
}

const HomePage = () => {
    const [gyms, setGyms] = useState<Gym[]>([]);
    const [open, setOpen] = useState(false);
    const [editingGym, setEditingGym] = useState<Gym | null>(null);
    const [form, setForm] = useState({
        type: '',
        number: '',
        address: '',
    });

    const fetchGyms = () => {
        axios.get('http://localhost:8080/api/gyms')
            .then(response => setGyms(response.data))
            .catch(error => console.error('Ошибка при загрузке залов:', error));
    };

    useEffect(() => {
        fetchGyms();
    }, []);

    const handleOpenAdd = () => {
        setEditingGym(null);
        setForm({ type: '', number: '', address: '' });
        setOpen(true);
    };

    const handleEdit = (gym: Gym) => {
        setEditingGym(gym);
        setForm({ type: gym.type, number: gym.number, address: gym.address });
        setOpen(true);
    };

    const handleDelete = (id: number) => {
        axios.delete(`http://localhost:8080/api/gyms/${id}`)
            .then(fetchGyms)
            .catch(error => console.error('Ошибка при удалении зала:', error));
    };

    const handleSave = () => {
        const payload = { ...form };
        if (editingGym) {
            axios.put(`http://localhost:8080/api/gyms/${editingGym.id}`, payload)
                .then(() => {
                    fetchGyms();
                    setOpen(false);
                });
        } else {
            axios.post('http://localhost:8080/api/gyms', payload)
                .then(() => {
                    fetchGyms();
                    setOpen(false);
                });
        }
    };

    return (
        <>
            <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
                <Typography variant="h4">Список залов</Typography>
                <Button variant="contained" onClick={handleOpenAdd}>Добавить зал</Button>
            </Box>

            <Box
                display="grid"
                gridTemplateColumns="repeat(auto-fit, minmax(300px, 1fr))"
                gap={2}
            >
                {gyms.map((gym) => (
                    <GymCard key={gym.id} gym={gym} onEdit={handleEdit} onDelete={handleDelete} />
                ))}
            </Box>

            <Dialog open={open} onClose={() => setOpen(false)} fullWidth>
                <DialogTitle>{editingGym ? 'Редактировать зал' : 'Добавить зал'}</DialogTitle>
                <DialogContent>
                    <TextField
                        label="Тип зала"
                        fullWidth
                        margin="dense"
                        value={form.type}
                        onChange={e => setForm({ ...form, type: e.target.value })}
                    />
                    <TextField
                        label="Номер"
                        fullWidth
                        margin="dense"
                        value={form.number}
                        onChange={e => setForm({ ...form, number: e.target.value })}
                    />
                    <TextField
                        label="Адрес"
                        fullWidth
                        margin="dense"
                        value={form.address}
                        onChange={e => setForm({ ...form, address: e.target.value })}
                    />
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setOpen(false)}>Отмена</Button>
                    <Button onClick={handleSave} variant="contained">Сохранить</Button>
                </DialogActions>
            </Dialog>
        </>
    );
};

export default HomePage;
