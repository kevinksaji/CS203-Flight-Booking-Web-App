import * as React from 'react';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import Modal from '@mui/material/Modal';
import WarningIcon from '@mui/icons-material/Warning';
import Grid from '@mui/material/Grid'
import {Button} from "@mui/material";

const style = {
    position: 'absolute',
    top: '50%',
    left: '50%',
    transform: 'translate(-50%, -50%)',
    width: 300,
    height: 300,
    backgroundColor: '#143965',
    border: '2px solid #000',
    boxShadow: 24,
    p: 4,
    borderRadius: "10px",
    direction: "column"

};

export default function BasicModal({openState, handleClose}) {


    const handleModalClose = () => {

        handleClose();
        }

    return (
        <div>
            <Modal
                open={openState}
                aria-labelledby="modal-modal-title"
                aria-describedby="modal-modal-description"

            >
                <Box sx={style}>
                    <Grid container sx={{
                        justifyContent: "center",
                        alignItems: "center",
                        direction: "column"
                    }}>
                        <Grid item>
                    <WarningIcon sx={{color: "#FF9900", height: "50px", width: "50px" }} />
                        </Grid>
                        <Grid item>
                    <Typography id="modal-modal-title" variant="h6" component="h2" fontFamily={"Merriweather Sans"} color={"white"}>
                        Flight taken
                    </Typography>
                        </Grid>
                        <Grid item>
                    <Typography id="modal-modal-description" sx={{ mt: 2, fontFamily:"Merriweather Sans", color:"white" }}>
                        Unfortunately, the flight you have selected no longer has sufficient seats for your party size. Please select another flight.
                    </Typography>
                        </Grid>
                    </Grid>
                    <Box sx={{alignItems: "center", justifyContent: "center", direction: "column", display: "flex", paddingTop: "100px"}}>
                    <Button sx={{backgroundColor: "#FF9900", color: "white"}} onClick={handleModalClose}> Okay</Button>
                    </Box>
                </Box>
            </Modal>
        </div>
    );
}