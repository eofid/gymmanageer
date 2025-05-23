import {
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Paper,
    IconButton,
    Tooltip
} from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';

interface Gym {
    id: number;
    type: string;
}

interface Client {
    id: number;
    name: string;
    phoneNumber: string;
    gym?: Gym;
}

interface Props {
    clients: Client[];
    onEdit: (client: Client) => void;
    onDelete: (id: number) => void;
}

const ClientsTable: React.FC<Props> = ({ clients, onEdit, onDelete }) => {
    return (
        <TableContainer component={Paper}>
            <Table>
                <TableHead>
                    <TableRow>
                        <TableCell>Имя</TableCell>
                        <TableCell>Телефон</TableCell>
                        <TableCell>Тип зала</TableCell>
                        <TableCell align="right">Действия</TableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    {clients.map((client) => (
                        <TableRow key={client.id}>
                            <TableCell>{client.name}</TableCell>
                            <TableCell>{client.phoneNumber}</TableCell>
                            <TableCell>{client.gym?.type || '—'}</TableCell>
                            <TableCell align="right">
                                <Tooltip title="Редактировать">
                                    <IconButton
                                        onClick={() => onEdit(client)}
                                        sx={{
                                            color: 'rgba(30, 136, 229, 0.7)',
                                            '&:hover': {
                                                backgroundColor: 'rgba(30, 136, 229, 0.1)'
                                            }
                                        }}
                                    >
                                        <EditIcon />
                                    </IconButton>
                                </Tooltip>
                                <Tooltip title="Удалить">
                                    <IconButton
                                        onClick={() => onDelete(client.id)}
                                        sx={{
                                            color: 'rgba(255, 0, 0, 0.7)',
                                            '&:hover': {
                                                backgroundColor: 'rgba(255, 0, 0, 0.1)'
                                            }
                                        }}
                                    >
                                        <DeleteIcon />
                                    </IconButton>
                                </Tooltip>
                            </TableCell>
                        </TableRow>
                    ))}
                </TableBody>
            </Table>
        </TableContainer>
    );
};

export default ClientsTable;
