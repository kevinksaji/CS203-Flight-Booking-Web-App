import { useEffect,  useState } from "react";
import {Box, Grid, Typography} from "@mui/material";
import ReturnPopUp from "../return-pop-up/ReturnPopUp";

const SECOND = 1000;
const MINUTE = SECOND * 60;

export const Timer = ({deadline}) => {
    // const parsedDeadline = useMemo(() => Date.parse(deadline), [deadline]);
    const [time, setTime] = useState(deadline - Date.now());
    const [displayModal, setDisplayModal] = useState(false);


    useEffect(() => {
        const interval = setInterval(
            () => setTime(deadline - Date.now()),
            1000,
        );
        if (time <= 0 ) {
            console.log("time is up!")
            setDisplayModal(true);
        }

        return () => clearInterval(interval);
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [time]);

    const timeUnits = [
        { label: "Minutes", divisor: MINUTE },
        { label: "Seconds", divisor: SECOND },
    ];

    return (
        <Grid container className="timer" spacing={2}>
            <ReturnPopUp openState={displayModal}/>
            {timeUnits.map(({ label, divisor }) => (
                <Grid key={label} item xs={6} sm={3}>
                    <Box display="flex" flexDirection="column" alignItems="flex-end" paddingLeft={"70px"}>
                        <Typography variant="h4" style={{ color: "white" }}>
                            {`${Math.floor((time / divisor) % 60)}`.padStart(2, "0")}
                        </Typography>
                        <Typography variant="body1" color="white">
                            {label}
                        </Typography>
                    </Box>
                </Grid>
            ))}
        </Grid>
    );
};

export default Timer;