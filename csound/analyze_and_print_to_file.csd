<CsoundSynthesizer>

<CsOptions>
-oaudio_output/dummy.wav
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
	a1		soundin Sfile

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
#include "includes/amplitude_tracker_offline.inc"
#include "includes/amplitude_tracker_parameters_offline.inc"

</CsInstruments>

<CsScore>
#define SCORELEN #20#
i1	0.1	1		                                                                        ; init analysis parameters
i2	4	$SCORELEN	"audio_input/WhiteNoise.wav"	1	"../analysis_output/source.csv" ; run analysis
i2	4	$SCORELEN	"audio_output/result.wav"	1	"../analysis_output/result.csv" ; run analysis

e
</CsScore>
</CsoundSynthesizer>
