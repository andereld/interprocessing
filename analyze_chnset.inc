;    Copyright 2015 Oeyvind Brandtsegg 
;
;    This file is part of the Signal Interaction Toolkit
;
;    The Signal Interaction Toolkit is free software: you can redistribute it and/or modify
;    it under the terms of the GNU General Public License version 3 
;    as published by the Free Software Foundation.
;
;    The Signal Interaction Toolkit is distributed in the hope that it will be useful,
;    but WITHOUT ANY WARRANTY; without even the implied warranty of
;    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;    GNU General Public License for more details.
;
;    You should have received a copy of the GNU General Public License
;    along with The Signal Interaction Toolkit.  
;    If not, see <http://www.gnu.org/licenses/>.

        Srms            sprintf {{rms%i}}, isource_chan
        Srms_preEq      sprintf {{rms_preEq%i}}, isource_chan
        Scps            sprintf {{cps%i}}, isource_chan
        Spitch          sprintf {{pitch%i}}, isource_chan
        Scentroid       sprintf {{centroid%i}}, isource_chan
        Sflatness       sprintf {{flatness%i}}, isource_chan
        Sspread         sprintf {{spread%i}}, isource_chan
        Sskewness       sprintf {{skewness%i}}, isource_chan
        Skurtosis       sprintf {{kurtosis%i}}, isource_chan
        Screst          sprintf {{crest%i}}, isource_chan
        Sflux           sprintf {{flux%i}}, isource_chan
        Sautocorr       sprintf {{autocorr%i}}, isource_chan
        Samp_trans      sprintf {{amp_trans%i}}, isource_chan
        Scentr_trans    sprintf {{centr_trans%i}}, isource_chan
        Skurt_trans     sprintf {{kurt_trans%i}}, isource_chan
        Spitchup_trans  sprintf {{pitchup_trans%i}}, isource_chan
        Spitchdown_trans sprintf {{pitchdown_trans%i}}, isource_chan
        Scps_raw        sprintf {{cps_raw%i}}, isource_chan

                	chnset krms, Srms               	
                	chnset krms_preEq, Srms_preEq
                        chnset kcps_n, Scps            
                        chnset kpitch_n, Spitch            
                        chnset kcentroid_n, Scentroid       
                	chnset kspread_n, Sspread         
                	chnset kskewness_n, Sskewness       
                	chnset kurtosis_n, Skurtosis       
                	chnset kflatness, Sflatness       
                	chnset kcrest_n, Screst          
                	chnset kflux_n, Sflux           
                        chnset kamp_trans, Samp_trans          
                        chnset kcentr_trans, Scentr_trans        
                        chnset kkurt_trans, Skurt_trans
                        chnset kpitchup_trans, Spitchup_trans
                        chnset kpitchdwn_trans, Spitchdown_trans
                        chnset kcps, Scps_raw   
