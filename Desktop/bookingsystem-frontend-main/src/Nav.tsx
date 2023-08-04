import * as React from "react";
import MenuIcon from "@mui/icons-material/Menu";
import { useState } from "react";
import Menu from "@mui/material/Menu";
import MenuItem from "@mui/material/MenuItem";
import Drawer from "@mui/material/Drawer";
import useMenu from "./useMenu";
import List from "@mui/material/List";
import Divider from "@mui/material/Divider";
import ListItem from "@mui/material/ListItem";
import ListItemButton from "@mui/material/ListItemButton";
import ListItemIcon from "@mui/material/ListItemIcon";
import ListItemText from "@mui/material/ListItemText";
import InboxIcon from "@mui/icons-material/MoveToInbox";
import MailIcon from "@mui/icons-material/Mail";
import Avatar from "@mui/material/Avatar";
import Stack from "@mui/material/Stack";
import { deepOrange } from "@mui/material/colors";
import { Link } from "react-router-dom";
import "./Nav.css";

const Nav = () => {
  const [isDrawerOpen, setIsDrawerOpen] = useState<boolean>(false);
  const toggleDrawer = (): void => {
    setIsDrawerOpen((open: boolean) => !open);
  };

  const { anchorEl, open, handleClose, handleOpenMenu } = useMenu();

  return (
    
    <nav className="navbar">
      <div>
        <MenuIcon onClick={toggleDrawer} />
        <Drawer
          anchor="left"
          open={isDrawerOpen}
          onClose={toggleDrawer}
          style={{ marginRight: "4px", width: "40%" }}
        >
          <div>
            <List>
              {["Home"].map((text, index) => (
                <ListItem key={text} disablePadding>
                  <ListItemButton>
                    <ListItemText primary={text} />
                  </ListItemButton>
                </ListItem>
              ))}
            </List>
            <Divider />
            <List>
              {["Text", "Text", "Text", "Text"].map((text, index) => (
                <ListItem key={text} disablePadding>
                  <ListItemButton>
                    <ListItemIcon>
                      <MailIcon />
                    </ListItemIcon>
                    <ListItemText primary={text} />
                  </ListItemButton>
                </ListItem>
              ))}
            </List>
            <Divider />
            <List>
              {["Text", "Text", "Text"].map((text, index) => (
                <ListItem key={text} disablePadding>
                  <ListItemButton>
                    <ListItemIcon>
                      <MailIcon />
                    </ListItemIcon>
                    <ListItemText primary={text} />
                  </ListItemButton>
                </ListItem>
              ))}
            </List>
          </div>
        </Drawer>
      </div>

      <div className="navbar__user" onClick={handleOpenMenu}>
        <Avatar
          style={{ marginRight: "4px" }}
          sx={{ bgcolor: deepOrange[500] }}
        >
          H
        </Avatar>
        <span> Minh</span>
      </div>
     
      <Menu anchorEl={anchorEl} open={open} onClose={handleClose} >
        
       
          <MenuItem onClick={handleClose}>Profile</MenuItem>
          <MenuItem onClick={handleClose}>My account</MenuItem>
        <Link to="/Logout">
          {" "}
          <MenuItem onClick={handleClose}>Logout</MenuItem>
        </Link>
      </Menu>
    </nav>
   
  );
};

export default Nav;
