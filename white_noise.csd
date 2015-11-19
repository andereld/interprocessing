<CsoundSynthesizer>

<CsOptions>
-odac
</CsOptions>

<CsInstruments>
	sr	= 44100
	ksmps	= 32
	nchnls	= 1
	0dbfs	= 1

	instr 1
	asig	noise 1, 0
		out asig
	endin
</CsInstruments>

<CsScore>
i1	0	7.25
</CsScore>

</CsoundSynthesizer>

