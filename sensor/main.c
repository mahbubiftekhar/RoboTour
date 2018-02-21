#ifndef F_CPU
#define F_CPU 20000000UL // or whatever may be your frequency
#endif
 
#include <avr/io.h>
#include <util/delay.h>                // for _delay_ms()
#include <avr/interrupt.h>
#include <util/twi.h>

#include "I2C_slave.h"

#define I2C_ADDR 0x32
#define NUM_SENSOR 6 

// volatile uint8_t buffer_address;
// volatile uint8_t txbuffer[0xFF];
// volatile uint8_t rxbuffer[0xFF];

void setup_adc(void);
void select_adc_channel(uint8_t);
void adc_start_conversion(void);
void setup_i2c(uint8_t);
void setup_led_pwm(void);

// index of the current sensor sent
volatile uint8_t current_sensor_s;
// index of the current sensor read
volatile uint8_t current_sensor_r;
// define a map corresponding to physical layout of the sensor (left to right)
// current_sensor_r is the index of the sensor, sensor
const uint8_t sensor_channel[NUM_SENSOR] = {7, 6, 0, 1, 2, 3};

volatile uint8_t sensor_value[NUM_SENSOR];

volatile uint8_t del = 50;

int main(void)
{
    DDRD = 0x00 | (1<<5);
                           // initialize port C
    uint8_t val;

    setup_adc();
    setup_led_pwm();
    setup_i2c(I2C_ADDR);

    
    txbuffer[0] = 1;
    txbuffer[1] = 1;
    txbuffer[2] = 1;
    txbuffer[3] = 1;
    txbuffer[4] = 1;
    txbuffer[5] = 1;
    txbuffer[6] = 1;

    I2C_init(0x32);
    
    sei();
    adc_start_conversion();
    OCR0B = 255;


    while(1)
    {
        OCR0B=255-OCR0B;

        //if(TWCR & (1<<TWINT)) del = 255;
        //else del = 50;


        val = 0;
        for(uint8_t i = 0; i < NUM_SENSOR; ++i)
        {
            txbuffer[i]=sensor_value[i];
            if(sensor_value[i] < 85)
            {
                val += 42;
            }
        }
        // OCR0B = 255-val;
        for(uint8_t i = 0; i < del; ++i)
        {
            //_delay_ms(10);
        }

        /*
        if(PINC & (1<<PC4))
        {
            OCR0B = 255;
        }
        else
        {
            OCR0B = 0;
        }
        */

    }
}

// interrupt routine for adc conversion complete
ISR(ADC_vect)
{
    sensor_value[current_sensor_r] = ADCH;
    current_sensor_r = (current_sensor_r+1)%NUM_SENSOR;

    select_adc_channel(sensor_channel[current_sensor_r]);
    adc_start_conversion();
}

// Interrupt routine for I2C/TWI events
/*
ISR(TWI_vect){
    
    // temporary stores the received data
    uint8_t data;
    
    // own address has been acknowledged
    if( (TWSR & 0xF8) == TW_SR_SLA_ACK ){  
        buffer_address = 0xFF;
        // clear TWI interrupt flag, prepare to receive next byte and acknowledge
        TWCR |= (1<<TWIE) | (1<<TWINT) | (1<<TWEA) | (1<<TWEN); 
    }
    else if( (TWSR & 0xF8) == TW_SR_DATA_ACK ){ // data has been received in slave receiver mode
        
        // save the received byte inside data 
        data = TWDR;
        
        // check wether an address has already been transmitted or not
        if(buffer_address == 0xFF){
            
            buffer_address = data; 
            
            // clear TWI interrupt flag, prepare to receive next byte and acknowledge
            TWCR |= (1<<TWIE) | (1<<TWINT) | (1<<TWEA) | (1<<TWEN); 
        }
        else{ // if a databyte has already been received
            
            // store the data at the current address
            rxbuffer[buffer_address] = data;
            
            // increment the buffer address
            buffer_address++;
            
            // if there is still enough space inside the buffer
            if(buffer_address < 0xFF){
                // clear TWI interrupt flag, prepare to receive next byte and acknowledge
                TWCR |= (1<<TWIE) | (1<<TWINT) | (1<<TWEA) | (1<<TWEN); 
            }
            else{
                // Don't acknowledge
                TWCR &= ~(1<<TWEA); 
                // clear TWI interrupt flag, prepare to receive last byte.
                TWCR |= (1<<TWIE) | (1<<TWINT) | (1<<TWEN); 
            }
        }
    }
    else if( (TWSR & 0xF8) == TW_ST_DATA_ACK ){ // device has been addressed to be a transmitter
        
        // copy data from TWDR to the temporary memory
        data = TWDR;
        
        // if no buffer read address has been sent yet
        if( buffer_address == 0xFF ){
            buffer_address = data;
        }
        
        // copy the specified buffer address into the TWDR register for transmission
        TWDR = txbuffer[buffer_address];
        // increment buffer read address
        buffer_address++;
        
        // if there is another buffer address that can be sent
        if(buffer_address < 0xFF){
            // clear TWI interrupt flag, prepare to send next byte and receive acknowledge
            TWCR |= (1<<TWIE) | (1<<TWINT) | (1<<TWEA) | (1<<TWEN); 
        }
        else{
            // Don't acknowledge
            TWCR &= ~(1<<TWEA); 
            // clear TWI interrupt flag, prepare to receive last byte.
            TWCR |= (1<<TWIE) | (1<<TWINT) | (1<<TWEN); 
        }
        
    }
    else{
        // if none of the above apply prepare TWI to be addressed again
        TWCR |= (1<<TWIE) | (1<<TWEA) | (1<<TWEN);
    } 
}
*/
/*

ISR(TWI_vect)
{
    del = 1;
    uint8_t status = TWSR & 0xF8;
    // examine the status register, masking out the prescaler bits
    switch(status)
    {
        // Slave address received. New read cycle. Load first sensor data
        case(TW_ST_SLA_ACK):
            current_sensor_s = 0;
        // Data byte transmitted, ack received
        case(TW_ST_DATA_ACK):
            TWDR = sensor_value[current_sensor_s];
            current_sensor_s++;
            del=10;

            // check if all bytes have been transmitted
            if(current_sensor_s == NUM_SENSOR)
            {
                // last byte - expect no ACK on next transmission cycle
                TWCR &= ~(1<<TWEA);
            }
            else
            {
                // expect ACK on next data byte
                TWCR |= (1<<TWEA);
            }
            break;

        case(TW_ST_LAST_DATA):
        case(TW_ST_DATA_NACK):
        default:
            del = 1;
            // enable ACK response to slave address detection
            TWCR |= (1<<TWEA);
            break;

    }
    // Write 1 to reset the interrupt flag and resume TWI operation
    TWCR |= (1<<TWINT) | (1<<TWEN) | (1<<TWIE); 
}

*/
void setup_adc(void)
{
    // Conversion clock needs to run between 50kHz and 200kHz
    //  @ 1MHz  select 1/8   for ~125kHz | 100
    //  @ 20MHz select 1/128 for ~156kHz | 111

    ADCSRA |= (1<<ADPS2)|(1<<ADPS1)|(1<<ADPS0);

    // Reference voltage selection - 00 for AREF
    ADMUX |= (0<<REFS1) | (0<<REFS0);

    // left-adjust the result for 8-bit resolution
    ADMUX |= (1<<ADLAR);

    // Enable ADC interrupt
    ADCSRA |= (1<<ADIE);
    
    // Enable the adc.
    ADCSRA |= (1<<ADEN);

    // TODO: look at digital input disable register

}

void adc_start_conversion(void)
{
    ADCSRA |= (1<<ADSC);
}

void select_adc_channel(const uint8_t channel) 
{
    // reset current channel selection (4 LSBs)
    ADMUX &= ~(0x0F);
    // set new channel but protecting other bits
    ADMUX |= channel & 0x0F;

    // note that the result might be surprising if channel > 15
}

void setup_i2c(const uint8_t slave_address)
{
    // set the slave addres
    TWAR |= (slave_address << 1);
    //TWAMR |= 0xF6;
    // enable the interrupts, acknowledge generation and the interface
    //TWCR = 0x45;
    TWCR |= (1<<TWEA) | (1<<TWIE) | (1<<TWEN) | (1<<TWINT);
}

void setup_led_pwm(void)
{
    // use TC0 in Phase correct pwm mode
    TCCR0A |= (1<<WGM00);
    // use OC0B as the pwm output
    TCCR0A |= (1<<COM0B1) | (0<<COM0B1);
    // set prescaler to 1/8
    TCCR0B |= (1<<CS01);
}