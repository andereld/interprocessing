# interprocessing

This code was written for my master's thesis. For a description of the problem
the problem it solves, please see the [report][1].

To run the program, you need to have Csound and Java installed. There is a
packaged JAR file available in this repository. The program takes a number of
arguments, three of which are required: The input audio file, the affecting
audio file and the name of one of the available effects. In order to see more
information, you can pass the `--help` flag:

```
java -jar interprocessing-1.0-standalone.jar --help

  -i, --affected AFFECTED_AUDIO_FILE                  Input audio file to be affected by FX processing.
  -a, --affector AFFECTING_AUDIO_FILE                 Audio file whose analyzed features will affect the FX processing.
  -d, --debug                                         Print debug information while running.
  -e, --effect EFFECT_NAME                            The effect to be applied.  One of: lp-distortion, bp-ringmod, gain
  -f, --frame-size FRAME_SIZE                         The duration of each frame to be analyzed processed. If no frame size
                                                      is given, the entire input file is processed in one pass using static
                                                      effect parameters.
  -h, --help                                          Print this help message.
  -m, --max-iterations MAX_ITERATIONS  100            Maximum number of iterations without change for a run of the genetic
                                                      algorithm.
  -o, --output-dir OUTPUT_DIRECTORY    output         The directory in which output audio and analysis files should be placed.
  -w, --weights FEATURE_WEIGHTS        [0.0 0.0 1.0]  Three weights separated by commas representing the importance of the mean
                                                      centroid, pitch and RMS amplitude values in the evaluation of fitness for
                                                      a set of FX parameters. The values must appear in-order, must not contain
                                                      negative numbers and must contain at least one value greater than zero.
```

[1]: #
