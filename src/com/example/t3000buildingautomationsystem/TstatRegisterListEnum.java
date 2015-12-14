package com.example.t3000buildingautomationsystem;




public enum TstatRegisterListEnum {
	
	MODBUS_ADDRESS("MODBUS_ADDRESS",6),
	
	MODBUS_COOL_HEAT_MODE("MODBUS_COOL_HEAT_MODE",101),   // -	-	Heating or cooling mode in effect	0 = coasting, 1 = cooling, 2 = heating
	MODBUS_MODE_OPERATION("MODBUS_MODE_OPERATION",102),  // -	-	Current state of Tstat.  High heat -> coasting -> high cool.
	MODBUS_SEQUENCE("MODBUS_SEQUENCE",103),			// 0	2	Sequence of operation , tstat behaves differently according to sequence
	MODBUS_DEGC_OR_F("MODBUS_DEGC_OR_F",104),			// 0	1	Temperature units  0 = DegC, 1 = DegF 
	MODBUS_FAN_MODE("MODBUS_FAN_MODE",105),			// 0	3	Number of fan speeds to show on the display 0 = 0 speeds, 3 = 3 speeds
	MODBUS_POWERUP_MODE("MODBUS_POWERUP_MODE",106),		// 0	3	Powerup mode.  0=Off, 2=On, 2=Last Value, 3=Auto
	MODBUS_AUTO_ONLY("MODBUS_AUTO_ONLY",107),			// 0	1	Enable or disable manual modes.  0=manual allowed, 1=auto only,2 = DDC mode
	MODBUS_FACTORY_DEFAULTS("MODBUS_FACTORY_DEFAULTS",108),   			// 0	1	Factory defaults  0=no default	
	MODBUS_INFO_BYTE("MODBUS_INFO_BYTE",109),						// -	-	Byte that holds info about the tstat
	MODBUS_BAUDRATE("MODBUS_BAUDRATE",110),						// 0	1	Baudrate 0 = 9.6kb/s, 1 = 19.2kb/s
	MODBUS_OVERRIDE_TIMER("MODBUS_OVERRIDE_TIMER",111),					// 0	255	Determines what controls the state of the LED
	MODBUS_OVERRIDE_TIMER_LEFT("MODBUS_OVERRIDE_TIMER_LEFT",112),
	MODBUS_HEAT_COOL_CONFIG("MODBUS_HEAT_COOL_CONFIG",113),
	MODBUS_TIMER_ON("MODBUS_TIMER_ON",114),
	MODBUS_TIMER_OFF("MODBUS_TIMER_OFF",115),
	MODBUS_TIMER_UNITS("MODBUS_TIMER_UNITS",116),
	MODBUS_DEAD_MASTER("MODBUS_DEAD_MASTER",117),
	MODBUS_SYSTEM_TIME_FORMAT("MODBUS_SYSTEM_TIME_FORMAT",118),			//		  system format
	MODBUS_FREE_COOL_CONFIG("MODBUS_FREE_COOL_CONFIG",119),				//		 Free cool configure register.bit0,free cool enable/disable,0 = disable,1= enable.
	MODBUS_RS485_MODE("MODBUS_RS485_MODE",120),//120	
	MODBUS_TEMPRATURE_CHIP("MODBUS_TEMPRATURE_CHIP",121),  //MODBUS_TEMPRATURE_SENSOR, //101/121 Calibrated temperature chip reading	(0.1 degrees)
	
	MODUBS_HUMIDITY_RH("MODUBS_HUMIDITY_RH",197),	       //			// relative humidity in percentage
	MODBUS_HUMIDITY_FREQUENCY("MODBUS_HUMIDITY_FREQUENCY",198), //		// raw frequency reading
	MODBUS_HUM_PIC_UPDATE("MODBUS_HUM_PIC_UPDATE",199),     //			// write current calibration table to PIC, which table decided by register 427
	MODBUS_HUM_CAL_NUM("MODBUS_HUM_CAL_NUM",200),	       //			// calibration data number
	MODBUS_HUM_CURRENT_DEFAULT("MODBUS_HUM_CURRENT_DEFAULT",201),//			// decide which table will run, default tabel or customer table   current=1 default=0	
	
	MODBUS_COOLING_VALVE("MODBUS_COOLING_VALVE",197),          //  TODO  寄存器值需确认，下同    // -	-	Cooling valve position 0-1000 = 0-10V
	MODBUS_HEATING_VALVE("MODBUS_HEATING_VALVE",197),          //  TODO      // -	-	Heating valve position 0-1000 = 0-10V	
	
	MODBUS_FAN_SPEED("MODBUS_FAN_SPEED",273),// 	    // 0	4	Fan speed 0=OFF, 1=Low, 2=MED, 3=HI, 4=AUTO
	
	MODBUS_DEFAULT_SETPOINT("MODBUS_DEFAULT_SETPOINT",341),		//
	MODBUS_SETPOINT_CONTROL("MODBUS_SETPOINT_CONTROL",342),		//
	MODBUS_DAYSETPOINT_OPTION("MODBUS_DAYSETPOINT_OPTION",343),		//	
	MODBUS_MIDDLE_SETPOINT("MODBUS_MIDDLE_SETPOINT",344),			//
	MODBUS_DAY_SETPOINT("MODBUS_DAY_SETPOINT",345),
	MODBUS_DAY_COOLING_DEADBAND("MODBUS_DAY_COOLING_DEADBAND",346),        //   	// 1	100	Cooling deadband	(0.1 degree)
	MODBUS_DAY_HEATING_DEADBAND("MODBUS_DAY_HEATING_DEADBAND",347),    	// 		// 1	100	Heating deadband	(0.1 degree)
	MODBUS_DAY_COOLING_SETPOINT("MODBUS_DAY_COOLING_SETPOINT",348),		//		// 15	50	(59 and 99 for degrees F)	Cooling setpoint (1degree)
	MODBUS_DAY_HEATING_SETPOINT("MODBUS_DAY_HEATING_SETPOINT",349),  		//		// 10	35	(50 and 95 for degrees F)	Heating setpoint (1degree)
	MODBUS_NIGHT_SETPOINT("MODBUS_NIGHT_SETPOINT",350),
	MODBUS_APPLICATION("MODBUS_APPLICATION",351),//   	                // 0	1	0=Office, 1=Hotel or Residential
 	MODBUS_NIGHT_HEATING_DEADBAND("MODBUS_NIGHT_HEATING_DEADBAND",352),//		    // 0	35	(0 and 95 for degrees F)	Night heating setback (1 degree)
	MODBUS_NIGHT_COOLING_DEADBAND("MODBUS_NIGHT_COOLING_DEADBAND",353),//		    // 0	99	(0 and 95 for degrees F)	Night cooling setback (1 degree)
	MODBUS_NIGHT_HEATING_SETPOINT("MODBUS_NIGHT_HEATING_SETPOINT",354),//		    // 10	99	Night Heating Setpoint	(1 degree)
	MODBUS_NIGHT_COOLING_SETPOINT("MODBUS_NIGHT_COOLING_SETPOINT",355),//		    // 10	99	Night Cooling Setpoint	(1 degree)
	MODBUS_WINDOW_INTERLOCK_COOLING_SETPOINT("MODBUS_WINDOW_INTERLOCK_COOLING_SETPOINT",356),   //TBD
	MODBUS_WINDOW_INTERLOCK_HEATING_SETPOINT("MODBUS_WINDOW_INTERLOCK_HEATING_SETPOINT",357),	//TBD
	MODBUS_UNIVERSAL_NIGHTSET("MODBUS_UNIVERSAL_NIGHTSET",358),//                
	MODBUS_UNIVERSAL_SET("MODBUS_UNIVERSAL_SET",359),//                  // 1    254 reg 246 is the setpoint for universal PID 
	MODBUS_UNIVERSAL_HEAT_DB("MODBUS_UNIVERSAL_HEAT_DB",360),//universal heating deadband 
	MODBUS_UNIVERSAL_COOL_DB("MODBUS_UNIVERSAL_COOL_DB",361),//universal cooling deadband
	MODBUS_ECOMONY_COOLING_SETPOINT("MODBUS_ECOMONY_COOLING_SETPOINT",362),
	MODBUS_ECOMONY_HEATING_SETPOINT("MODBUS_ECOMONY_HEATING_SETPOINT",363),
	MODBUS_POWERUP_SETPOINT("MODBUS_POWERUP_SETPOINT",364),//		        // 15	50	(59 and 99 for degrees F)	Power up cooling setpoint (1 degree)
	MODBUS_MAX_SETPOINT("MODBUS_MAX_SETPOINT",365),//                   // 10	99	(50 and 150 for degrees F)	Max cooling setpoint (1degree)
	MODBUS_MIN_SETPOINT("MODBUS_MIN_SETPOINT",366),// 		            // 10	99	(50 and 150 for degrees F)	Min heating setpoint (1degree)
	MODBUS_MAX_SETPOINT2("MODBUS_MAX_SETPOINT2",367),//			// max and min setpoint for celling setpoint
	MODBUS_MIN_SETPOINT2("MODBUS_MIN_SETPOINT2",368),//
	MODBUS_MAX_SETPOINT3("MODBUS_MAX_SETPOINT3",369),//			// max and min setpoint for average setpoint 
	MODBUS_MIN_SETPOINT3("MODBUS_MIN_SETPOINT3",370),//	
	MODBUS_MAX_SETPOINT4("MODBUS_MAX_SETPOINT4",371),//
	MODBUS_MIN_SETPOINT4("MODBUS_MIN_SETPOINT4",372),//
	MODBUS_SETPOINT_INCREASE("MODBUS_SETPOINT_INCREASE",373),		// 
	MODBUS_FREEZE_TEMP_SETPOINT("MODBUS_FREEZE_TEMP_SETPOINT",374),	//
	MODBUS_WALL_SETPOINT("MODBUS_WALL_SETPOINT",375),//    wall setpoint ,normal setpoint
	MODBUS_CEILING_SETPOINT("MODBUS_CEILING_SETPOINT",376),//    celling setpoint
	MODBUS_AVERAGE_SETPOINT("MODBUS_AVERAGE_SETPOINT",377),//
	MODBUS_UNOCCUPIED_HEATING("MODBUS_UNOCCUPIED_HEATING",378),//
	MODBUS_UNOCCUPIED_COOLING("MODBUS_UNOCCUPIED_COOLING",379),//
	MODBUS_RH_SETPOINT("MODBUS_RH_SETPOINT",380),//
 	MODBUS_CURRENT1_SETPOINT("MODBUS_CURRENT1_SETPOINT",381),//  tbd  get rid of this
 	MODBUS_TEMP_SELECT("MODBUS_TEMP_SELECT",382);//			 		// 1= external sensor analog input 1 , 2 = internal thermistor, 3 = average the internal thermistor and analog input1
 	
	private String name;
	private int id;
	
	private TstatRegisterListEnum(String name, int id){
		this.name = name;
		this.id = id;
	}
	
	public static String getName(int id) {
        for (TstatRegisterListEnum c : TstatRegisterListEnum.values()) {
            if (c.getId() == id) {
                return c.name;
            }
        }
        return null;
    }
	
	public static int getIdByName(String name){
		for(TstatRegisterListEnum c:TstatRegisterListEnum.values()){
			if(c.getName().equals(name)){
				return c.id;
			}
		}
		return 0;
	}

    // get set 方法
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}