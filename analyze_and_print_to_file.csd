<CsoundSynthesizer>

<CsOptions>
-odac
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
#include "analyze_udos.inc"

	instr 1
#include "analyze_chn_init.inc"
	endin

	instr 2
	Sfile           strget p4
	isource_chan     = p5           
	al,ar		soundin Sfile
	a1	        = (0.5 * al) + (0.5 * ar)

#include "analyze_audio.inc"
#include "analyze_chnset.inc"

	Soutput		strget p6 ; Output CSV to this filename.
#include "analyze_print_to_file.inc"
	endin

;**************************
; subscribe to control channels (instr 4 to 8)
#include "subscriber_offline.inc"

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
#include "amplitude_tracker_offline.inc"
#include "amplitude_tracker_parameters_offline.inc"

</CsInstruments>

<CsScore>
#define SCORELEN #2#
i1	0.1	1						     ; init analysis parameters
i2	1	$SCORELEN	"test.wav"	1	"result.csv" ; run analysis

e
</CsScore>
</CsoundSynthesizer>
