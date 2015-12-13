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
<CsoundSynthesizer>

<CsOptions>
-oaudio_output/test.wav
</CsOptions>

<CsInstruments>
	sr = 44100
	ksmps = 256
	nchnls = 2
	0dbfs = 1

	giSine		ftgen	0, 0, 65536, 10, 1				; sine wave
	gifftsize 	= 1024
			chnset gifftsize, "fftsize"
	giFftTabSize	= (gifftsize / 2)+1
	gifna     	ftgen   1, 0, giFftTabSize, 7, 0, giFftTabSize, 0   	; for pvs analysis
	gifnf     	ftgen   2, 0, giFftTabSize, 7, 0, giFftTabSize, 0   	; for pvs analysis

;**************************
; analyze
#include "includes/analyze_udos.inc"

	instr 1
#include "includes/analyze_chn_init.inc"
	endin

	instr 2
	Sfile           strget p4
	isource_chan     = p5           
	a1	        soundin Sfile

#include "includes/analyze_audio.inc"
#include "includes/analyze_chnset.inc"

	Soutput		strget p6 ; Output CSV to this filename.
#include "includes/analyze_print_to_file.inc"
	endin

;**************************
; subscribe to control channels (instr 4 to 8)
#include "includes/subscriber_offline.inc"

;**************************
; set chn values (as if from gui)
	instr 21        ; set string chn value
	Schan           strget p4
	Sval            strget p5
	                chnset Sval, Schan
	endin
	
	instr 22        ; set float chn value
	Schan           strget p4
	ival            = p5
	                chnset ival, Schan
	endin

;**************************
; process audio
#include "includes/instrument_includes.inc"

</CsInstruments>

<CsScore>
#define SCORELEN #20#
i1	0.1		1			                                                                         ; init analysis parameters
#include "includes/analyze_affector.inc"

; assign analysis signals to efx parameters (as if from gui)
#include "includes/interconnections.inc"
#include "includes/instrument_score_events.inc"
#include "includes/effect_score_events.inc"

e
</CsScore>
</CsoundSynthesizer>
