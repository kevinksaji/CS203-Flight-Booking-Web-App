import * as React from "react";
import Box from "@mui/material/Box";
import Tab from "@mui/material/Tab";
import TabContext from "@mui/lab/TabContext";
import TabList from "@mui/lab/TabList";
import TabPanel from "@mui/lab/TabPanel";
import { Paper } from "@mui/material";
import BookingBox from "./BookingBox";
import UnderConstruction from "./UnderConstruction";

export default function LabTabs() {
  const [value, setValue] = React.useState("1");

  const handleChange = (event, newValue) => {
    setValue(newValue);
  };

  return (
    <Paper sx={{ backgroundColor: "white", elevation: 0 }}>
      <Box sx={{ width: "100%", typography: "body1" }} p={1} mt={2}>
        <TabContext value={value}>
          <Box sx={{ borderBottom: 1, borderColor: "divider" }}>
            <TabList onChange={handleChange} aria-label="Booking box">
              <Tab label="Book" value="1" />
              <Tab label="Manage" value="2" />
              <Tab label="Check in" value="3" />
            </TabList>
          </Box>
          <TabPanel value="1">
            <BookingBox />
          </TabPanel>
          <TabPanel value="2">
            <UnderConstruction />
          </TabPanel>
          <TabPanel value="3">
            <UnderConstruction />
          </TabPanel>
        </TabContext>
      </Box>
    </Paper>
  );
}
