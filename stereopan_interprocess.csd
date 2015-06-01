<Cabbage>
form size(575, 115), caption("stereopan"), pluginID("ispn")
image bounds(0, 0, 575, 115), file("background.jpg"), shape("round")
label text("port"), bounds(10, 10, 80, 12), identchannel("port_id")

groupbox bounds(5, 30, 564, 81), plant("plant_Pan"), linethickness("0"){ 
combobox channel("source1_Pan"), bounds(10, 12, 90, 20), items("rms", "rms_preEq", "cps", "pitch", "centroid", "spread", "skewness", "kurtosis", "flatness", "crest", "flux", "amp_trans", "centr_trans", "kurt_trans", "pitchup_trans", "pitchdown_trans"), value(1), channeltype("string")
combobox channel("chan1_Pan"), bounds(103, 12, 50, 20), items("1", "2", "3", "4"), value(1)
texteditor bounds(158, 14, 35, 15), channel("rise1_Pan"), colour(0,0,0,255), fontcolour("white"), text(0.01)
texteditor bounds(196, 14, 35, 15), channel("fall1_Pan"), colour(0,0,0,255), fontcolour("white"), text(0.5)
hslider bounds(233, 12, 86, 20), channel("scale1_Pan"), range(-1.0, 1.0, 0, 1, 0.01)
button bounds(320, 12, 29, 19), channel("scale1_x_Pan"), text("x 1","x 10"), 
hslider bounds(349, 12, 86, 20), channel("curve1_Pan"), range(-5.0, 5.0, 0)

combobox channel("source2_Pan"), bounds(10, 34, 90, 20), items("rms", "rms_preEq", "cps", "pitch", "centroid", "spread", "skewness", "kurtosis", "flatness", "crest", "flux", "amp_trans", "centr_trans", "kurt_trans", "pitchup_trans", "pitchdown_trans"), value(1), channeltype("string")
combobox channel("chan2_Pan"), bounds(103, 34, 50, 20), items("1", "2", "3", "4"), value(1)
texteditor bounds(158, 36, 35, 15), channel("rise2_Pan"), colour(0,0,0,255), fontcolour("white"), text(0.01)
texteditor bounds(196, 36, 35, 15), channel("fall2_Pan"), colour(0,0,0,255), fontcolour("white"), text(0.5)
hslider bounds(233, 34, 86, 20), channel("scale2_Pan"), range(-1.0, 1.0, 0, 1, 0.01)
button bounds(320, 34, 29, 19), channel("scale2_x_Pan"), text("x 1","x 10"), 
hslider bounds(349, 34, 86, 20), channel("curve2_Pan"), range(-5.0, 5.0, 0)

label bounds(10, 58, 90, 12), text("source"), colour(20,20,20,255)
label bounds(103, 58, 50, 12), text("chan"), colour(20,20,20,255)
label bounds(156, 58, 76, 12), text("rise/fall"), colour(20,20,20,255)
label bounds(236, 58, 110, 12), text("scale"), colour(20,20,20,255)
label bounds(352, 58, 81, 12), text("curve"), colour(20,20,20,255)

rslider bounds(433, 12, 62, 62), text("offset"), channel("offset_Pan"), range(0.0, 1.0, 0.5, 1, 0.001) 
combobox bounds(433, 1, 55, 12), channel("offsetx_Pan"), items("-1", "Nornm", "+1"), , value(2), channeltype("string")

rslider bounds(494, 8, 66, 66), text("Pan"), channel("Pan"), range(0.0, 1.0, 0.5, 1, 0.001)
}

;next x position available below plants is 115

</Cabbage>
<CsoundSynthesizer>
<CsOptions>
-n -d -+rtmidi=NULL -M0 -m0d
</CsOptions>
<CsInstruments>

        sr = 48000
	ksmps = 64
	nchnls = 2
	0dbfs = 1

	giSine	ftgen	0, 0, 65536, 10, 1			; sine wave
        gkhandle init -1
        gkport init -1
        ; list of source channels, used for checking which signals we are currently listening to
        giSourceActive  ftgen 0, 0, 32, 2, 0    ; store the channel numbers (analysis instances) currently listened to

#include "stereopan_parameter_ranges.inc"           ; instr 1
#include "subscriber_connect.inc"                   ; instr 2,3
#include "subscriber_receiver.inc"                  ; instr 4,5,6,7,8


;****************************************************************
; Stereo pan
;****************************************************************
	instr 99
	a1,a0		ins	
	kinlevl		= 1
	a1		= a1 *kinlevl
	kPan		chnget "Pan"
	aPan            interp kPan
	; sinoid equal power panning
	aAngle		=  1.57079633 * aPan
        aLeft		= a1 * sin(aAngle)
	aRight		= a1 * cos(aAngle)
			outs aLeft, aRight

	endin

</CsInstruments>
<CsScore>
f0 0.1 ; workaround so that the host VST scanning will not fail upon trying to open the same network port in several plugins
s
#define SCORELEN #86400#
i1      0.1     1               ; init arrays
i2	0.1	$SCORELEN
#include "stereopan_score_events.inc"
i99     0       $SCORELEN       ; effect
e
</CsScore>
</CsoundSynthesizer>
