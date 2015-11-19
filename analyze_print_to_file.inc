; Values set in analyze_audio.inc, headers (channel names) set in analyze_chnset.inc.
; The name of the output file (Soutput) must be set in the including context.

Sheaders	sprintf "%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n", \
		        Srms, Srms_preEq, Scps, Spitch, Scentroid, Sflatness, Sspread, \
		        Sskewness, Skurtosis, Screst, Sflux, Samp_trans, Scentr_trans, \
		       	Skurt_trans, Spitchup_trans, Spitchdown_trans, Scps_raw
fprints		Soutput, Sheaders
fprintks	Soutput, "%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f\n", \
		krms, krms_preEq, kcps_n, kpitch_n, kcentroid_n, kflatness, kspread_n, \
	       	kskewness_n, kurtosis_n, kcrest_n, kflux_n, kamp_trans, kcentr_trans, \
		kkurt_trans, kpitchup_trans, kpitchdwn_trans, kcps
