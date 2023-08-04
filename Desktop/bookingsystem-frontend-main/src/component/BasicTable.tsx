import * as React from 'react';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';

function createData(
  Monday: string,
  Tuesday: string,
  Wednesday: string,
  Thursday: string,
  Friday: string,
) {
  return { Monday, Tuesday, Wednesday, Thursday, Friday };
}

const rows = [
  createData('8:00-9:00', '8:00-9:00','8:00-9:00','8:00-9:00','8:00-9:00'),
  createData('9:00-10:00', '9:00-10:00', '9:00-10:00', '9:00-10:00', '9:00-10:00'),
  createData('10:00-11:00', '10:00-11:00', '10:00-11:00', '10:00-11:00', '10:00-11:00'),
  createData('11:00-12:00','11:00-12:00', '11:00-12:00', '11:00-12:00', '11:00-12:00'),
  createData('12:00-13:00', '12:00-13:00', '12:00-13:00', '12:00-13:00', '12:00-13:00'),
  createData('13:00-14:00', '13:00-14:00', '13:00-14:00', '13:00-14:00', '13:00-14:00'),
  createData('14:00-15:00', '14:00-15:00', '14:00-15:00', '14:00-15:00', '14:00-15:00'),

  
];

export default function BasicTable() {
  return (
    <TableContainer component={Paper}>
      <Table sx={{ minWidth: 650 }} size="small" aria-label="simple table">
        <TableHead>
          <TableRow>
            <TableCell align="center">Monday</TableCell>
            <TableCell align="center">Tuesday</TableCell>
            <TableCell align="center">Wednesday</TableCell>
            <TableCell align="center">Thursday</TableCell>
            <TableCell align="center">Friday</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {rows.map((row) => (
            <TableRow>
              
              <TableCell align="center">{row.Monday}</TableCell>
              <TableCell align="center">{row.Tuesday}</TableCell>
              <TableCell align="center">{row.Wednesday}</TableCell>
              <TableCell align="center">{row.Thursday}</TableCell>
              <TableCell align="center">{row.Friday}</TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
}