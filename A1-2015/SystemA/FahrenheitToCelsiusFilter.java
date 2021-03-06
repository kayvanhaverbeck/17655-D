/******************************************************************************************************************
* File:FahrenheitToCelsiusFilter.java
* Course: 17655
* Project: Assignment 1
* Copyright: Copyright (c) 2003 Carnegie Mellon University
* Versions:
*	1.0 November 2008 - Sample Pipe and Filter code (ajl).
*
* Description:
*
* This class reads data from the filter's input port, converts the Fahrenheit measurement to Celsius
* using the calculation C = ((F- 32) * 5.0 / 9.0) and writes the converted data out the filter's output port.
*
* Parameters: 		None
*
* Internal Methods: None
*
******************************************************************************************************************/

public class FahrenheitToCelsiusFilter extends FilterFramework
{
	public void run()
    {
		int MeasurementLength = 8;		// This is the length of all measurements (including time) in bytes
		int IdLength = 4;				// This is the length of IDs in the byte stream

		int bytesread = 0;					// Number of bytes read from the input file.
		int byteswritten = 0;				// Number of bytes written to the stream.
		byte databyte = 0;					// The byte of data read from the file

		long measurement;				// This is the word used to store all measurements - conversions are illustrated.
		double valueCelsius;			//This is the converted measurement (in Celsius)
		double formattedCelsius; //This is the formatted Value with 5 decimal places
		int id;							// This is the measurement id
		int i;							// This is a loop counter

		// Next we write a message to the terminal to let the world know we are alive...

		System.out.print( "\n" + this.getName() + "::FahrenheitToCelsiusFilter Reading ");

		while (true)
		{
			/*************************************************************
			*	Here we read a byte and write a byte
			*************************************************************/

			try
			{
				id = 0;

				for (i=0; i<IdLength; i++ )
				{
					databyte = ReadFilterInputPort();	// This is where we read the byte from the stream...

					id = id | (databyte & 0xFF);		// We append the byte on to ID...

					if (i != IdLength-1)				// If this is not the last byte, then slide the
					{									// previously appended byte to the left by one byte
						id = id << 8;					// to make room for the next byte we append to the ID

					} // if

					bytesread++;						// Increment the byte count

					WriteFilterOutputPort(databyte);
					byteswritten++;

				} // for

				if ( id == 4 )
				{
					measurement = 0;

					for (i=0; i<MeasurementLength; i++ )
					{
						databyte = ReadFilterInputPort();
						measurement = measurement | (databyte & 0xFF);	// We append the byte on to measurement...

						if (i != MeasurementLength-1)					// If this is not the last byte, then slide the
						{												// previously appended byte to the left by one byte
							measurement = measurement << 8;				// to make room for the next byte we append to the
							// measurement
						} // if

						bytesread++;									// Increment the byte count

					} // if


					valueCelsius = (Double.longBitsToDouble(measurement) - 32) * 5.0 / 9.0; //convert the measurement from long to double and change to Celsius
					formattedCelsius = Math.round (valueCelsius * 100000.0) / 100000.0;   //formatting double to show only 5 decimal places
					measurement = Double.doubleToLongBits(formattedCelsius);  //convert double to long

					for(i = 0; i < MeasurementLength; i++) {
						databyte = (byte) ((measurement >> ((7 - i) * 8)) & 0xff);
						WriteFilterOutputPort(databyte);
						byteswritten++;
					}

				} // if

				else {
					databyte = ReadFilterInputPort();
					bytesread++;
					WriteFilterOutputPort(databyte);
					byteswritten++;
				}



			} // try

			catch (EndOfStreamException e)
			{
				ClosePorts();
				System.out.print( "\n" + this.getName() + "::FahrenheitToCelsiusFilter Exiting; bytes read: " + bytesread + " bytes written: " + byteswritten );
				break;

			} // catch

		} // while

   } // run

} // MiddleFilter
