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

;****************************************************************
; Tremolo/AM
;****************************************************************
	instr 99
	Sfile           strget p4
	a1	        soundin Sfile
	kinlevl		= 1
	a1		= a1 *kinlevl
	kDepth		chnget "Depth"
	kRateLow	chnget "RateLow"
	kRateHigh	chnget "RateHigh"
	aMod            oscil3 0.5, kRateLow+kRateHigh, giSine
	aMod            = (aMod+0.5)*kDepth
	aTrm            = a1*aMod
			outs aTrm, aTrm

	endin
