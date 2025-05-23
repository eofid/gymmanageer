// src/components/GymCard.tsx
import { Card, CardContent, Typography, IconButton, Box } from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';

interface Gym {
    id: number;
    type: string;
    number: string;
    address: string;
}

interface Props {
    gym: Gym;
    onEdit: (gym: Gym) => void;
    onDelete: (id: number) => void;
}

const GymCard: React.FC<Props> = ({ gym, onEdit, onDelete }) => {
    return (
        <Card>
            <CardContent>
                <Typography variant="h6">{gym.type}</Typography>
                <Typography>Номер: {gym.number}</Typography>
                <Typography>Адрес: {gym.address}</Typography>
                <Box display="flex" justifyContent="flex-end" mt={2}>
                    <IconButton onClick={() => onEdit(gym)} sx={{ mr: 1 }}>
                        <EditIcon />
                    </IconButton>
                    <IconButton onClick={() => onDelete(gym.id)} color="error">
                        <DeleteIcon />
                    </IconButton>
                </Box>
            </CardContent>
        </Card>
    );
};

export default GymCard;
