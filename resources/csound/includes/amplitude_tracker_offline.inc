; Takes the source audio file (p4) and a filehandle for its output audio (p5).
instr 101
Sfile		strget p4
a1		soundin Sfile
kinlevl		= 1

a1		= a1 * kinlevl
kGain		chnget "Gain"
ares		= a1 * kGain
		fout "audio_output/result.wav", 10, ares
		outs ares, ares
endin