import { useEffect, useState } from 'react';
import {
    Button,
    Typography,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    TextField,
    MenuItem,
    Box
} from '@mui/material';
import axios from 'axios';
import ClientsTable from '../components/ClientsTable';

interface Gym {
    id: number;
    type: string;
}

interface Client {
    id: number;
    name: string;
    phoneNumber: string;
    gym?: Gym;
    trainer?: {
        name: string;
    };
}

const ClientsPage = () => {
    const [clients, setClients] = useState<Client[]>([]);
    const [gyms, setGyms] = useState<Gym[]>([]);
    const [open, setOpen] = useState(false);
    const [editingClient, setEditingClient] = useState<Client | null>(null);
    const [formData, setFormData] = useState({
        name: '',
        phoneNumber: '',
        gymId: ''
    });

    useEffect(() => {
        fetchClients();
        fetchGyms();
    }, []);

    const fetchClients = async () => {
        try {
            const res = await axios.get('http://localhost:8080/api/persons');
            setClients(res.data);
        } catch (error) {
            console.error('Ошибка при загрузке клиентов:', error);
        }
    };

    const fetchGyms = async () => {
        try {
            const res = await axios.get('http://localhost:8080/api/gyms');
            setGyms(res.data);
        } catch (error) {
            console.error('Ошибка при загрузке залов:', error);
        }
    };

    const handleOpen = () => {
        setEditingClient(null);
        setFormData({ name: '', phoneNumber: '', gymId: '' });
        setOpen(true);
    };

    const handleEdit = (client: Client) => {
        setEditingClient(client);
        setFormData({
            name: client.name,
            phoneNumber: client.phoneNumber,
            gymId: client.gym?.id.toString() || ''
        });
        setOpen(true);
    };

    const handleDelete = async (id: number) => {
        try {
            await axios.delete(`http://localhost:8080/api/persons/${id}`);
            fetchClients();
        } catch (error) {
            console.error('Ошибка при удалении клиента:', error);
        }
    };

    const handleClose = () => {
        setOpen(false);
    };

    const handleSave = async () => {
        const payload = {
            name: formData.name,
            phoneNumber: formData.phoneNumber
        };

        try {
            let savedClient: Client;

            if (editingClient) {
                const res = await axios.put(
                    `http://localhost:8080/api/persons/${editingClient.id}`,
                    payload
                );
                savedClient = res.data;
            } else {
                const res = await axios.post('http://localhost:8080/api/persons', payload);
                savedClient = res.data;
            }

            if (formData.gymId) {
                await axios.put(
                    `http://localhost:8080/api/persons/${savedClient.id}/gym/${formData.gymId}`
                );
            }

            fetchClients();
            setOpen(false);
        } catch (error) {
            console.error('Ошибка при сохранении клиента:', error);
        }
    };

    return (
        <>
            <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
                <Typography variant="h4">Список клиентов</Typography>
                <Button variant="contained" onClick={handleOpen}>
                    Добавить клиента
                </Button>
            </Box>

            <ClientsTable clients={clients} onEdit={handleEdit} onDelete={handleDelete} />

            <Dialog open={open} onClose={handleClose}>
                <DialogTitle>{editingClient ? 'Редактировать клиента' : 'Добавить клиента'}</DialogTitle>
                <DialogContent>
                    <TextField
                        margin="dense"
                        label="Имя"
                        fullWidth
                        value={formData.name}
                        onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                    />
                    <TextField
                        margin="dense"
                        label="Телефон"
                        fullWidth
                        value={formData.phoneNumber}
                        onChange={(e) => setFormData({ ...formData, phoneNumber: e.target.value })}
                    />
                    <TextField
                        select
                        margin="dense"
                        label="Зал"
                        fullWidth
                        value={formData.gymId}
                        onChange={(e) => setFormData({ ...formData, gymId: e.target.value })}
                    >
                        {gyms.map((gym) => (
                            <MenuItem key={gym.id} value={gym.id}>
                                {gym.type}
                            </MenuItem>
                        ))}
                    </TextField>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleClose}>Отмена</Button>
                    <Button onClick={handleSave} variant="contained">
                        Сохранить
                    </Button>
                </DialogActions>
            </Dialog>
        </>
    );
};

export default ClientsPage;
