	sr	= 44100
	ksmps	= 256
	nchnls	= 1
	0dbfs	= 1

; Release flag channels. These signal to the host program that an analysis
; or processing run has completed.
	chn_k	"analysis-done", 2
	chn_k	"processing-done", 2

; Abort channel; a non-zero value here signals to the host program
; that no further processing should be performed because the end of
; an input file has been reached.
	chn_k	"abort", 2

; Analysis output channels (instr 1):
	chn_k	"centroid", 2
	chn_k	"cps", 2
	chn_k	"rms", 2

; FX parameter input channels:
;;; instr 2 -- LP filter and distortion
	chn_k	"coefficient-a", 1
	chn_k	"coefficient-b", 1
	chn_k	"cutoff", 1
	chn_k	"resonance", 1
;;; instr 3 -- volume control
	chn_k	"gain", 1
;;; instr 4 -- ring modulator and BP filter
	chn_k	"mod-freq", 1
	chn_k	"mod-depth", 1
	chn_k	"filter-freq", 1
	chn_k	"filter-bandwidth", 1
	chn_k	"post-gain", 1

; We need a number to signify that p3 should be set by the instrument itself;
; a duration of 118 years is unlikely to appear as a wanted value.
#define MAGIC #0xDEADBEEF#

#define INIT
#
; Set p3 to the duration of the input file if the given p3 is equal to $MAGIC.
	idur		filelen p5
	if p3 != $MAGIC	igoto continue
	p3		= idur
continue:

; If p4 (soundin's iskiptim argument) is greater than or equal to the duration
; of the input, we pass a non-zero value to the 'abort' channel. This signals
; to the host program that we've moved past the end of the input, i.e. that
; we've 'finished'. Because the host program's call to PerformKsmps() still
; needs to return, we'll keep on processing, leaving it to handle any excess
; data correctly.
	if p4 < idur	goto no_abort
			chnset 1.0, "abort"
no_abort:
#


; Analysis instrument; takes a mono audio file and outputs its
; CPS, centroid and RMS values at k-rate.
;	p4 -- iskiptim
;	p5 -- input file
	instr 1
	$INIT
			xtratim 1/kr
	kflag		release
			chnset kflag, "analysis-done"
	if kflag == 1	kgoto end

	ain		soundin p5, p4

	ifftsize	= 1024
; Calculate the (fundamental) pitch, spectral centroid and RMS amplitude:
	kcps, kamp	ptrack ain, ifftsize
	ktrig		= 1  ; always calculate new value
	kcentroid	centroid ain, ktrig, ifftsize
	krms		rms ain
	
; Send analysis values off to the host program:
			chnset kcentroid, "centroid"
			chnset kcps, "cps"
			chnset krms, "rms"
end:
	endin

; Combined distortion and filter effect; takes a mono audio file and
; processes it with modified tanh distortion and a resonant low-pass filter.
;	p4 -- iskiptim
;	p5 -- input file
;	p6 -- output file
	instr 2
	$INIT
			xtratim 1/kr
	kflag		release
			chnset kflag, "processing-done"
	if kflag == 1	kgoto end

	ain		soundin p5, p4

	kgain		= 1
	ka		chnget "coefficient-a"
	kb		chnget "coefficient-b"
	kcutoff		chnget "cutoff"
	kres		chnget "resonance"

        aout		= (exp(ain * (ka + kgain)) - exp(ain * (kb - kgain))) \
			  / (exp(ain * kgain) + exp(-ain * kgain))
	aout		lpf18 aout, kcutoff, kres, 1

			fout p6, 14, aout
end:
	endin

; A simple 'volume knob'; takes a mono audio file and adjusts its amplitude
; by the gain set on the 'gain' channel.
;	p4 -- iskiptim
;	p5 -- input file
;	p6 -- output file
	instr 3
	$INIT
			xtratim 1/kr
	kflag		release
			chnset kflag, "processing-done"
	if kflag == 1	kgoto end

	ain		soundin p5, p4
	kgain		chnget "gain"
	aout		= kgain * ain
			fout p6, 14, aout
end:
	endin

; Combined ring modulation and bandpass filtering; takes a mono audio file and
; runs it through a ring modulator, a bandpass filter and finally a gain stage
; to make up for a potentially significant loss of energy through the filter.
;	p4 -- iskiptim
;	p5 -- input file
;	p6 -- output file
	instr 4
	$INIT
			xtratim 1/kr
	kflag		release
			chnset kflag, "processing-done"
	if kflag == 1	kgoto end

	ain		soundin p5, p4

	kmodfreq	chnget "mod-freq"
	kmoddepth	chnget "mod-depth"
	kfilterfreq	chnget "filter-freq"
	kfilterbw	chnget "filter-bandwidth"
	kgain		chnget "post-gain"

	amod		poscil kmoddepth, kmodfreq
	aout		= ain * amod
	aout		butterbp aout, kfilterfreq, kfilterbw
	aout		= kgain * aout
			fout p6, 14, aout
end:
	endin
