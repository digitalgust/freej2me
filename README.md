
This is a fork of freej2me.    
Improvement:    
* Support network.
* Support java.microedition.lcdui.TextField and java.microedition.lcdui.TextBox for input .
* Support full of com.nokia.mid.ui.DirectGraphics.drawImage maniputation.
* Fixed java.microedition.lcdui package bugs.



# freej2me

![Java CI](https://github.com/hex007/freej2me/workflows/Java%20CI/badge.svg)

A free J2ME emulator with libretro, awt and sdl2 frontends.

Authors :
- David Richardson [Recompile@retropie]
- Saket Dandawate  [Hex@retropie]

---

## Controls

* `Q` and `W` for left and right softkeys.
* Arrow keys for nav, unless phone is set to "Standard", when arrow keys become 2, 4, 6, and 8.
* Numbers work as expected, the number pad is inverted (123 swap with 789, like a phone)
* `E` and `R` are alternatives to `*` and `#`.
* Enter functions as the Fire key or `5` on "Standard" mode
* ESC brings up the settings menu
* In the AWT frontend (freej2me.jar) `Ctrl+C` takes a screenshot and `+`/`-` can be used to control the window scaling factor

Click [here](KEYMAP.md) for information about more keybindings

## Links
Screenshots:
  https://imgur.com/a/2vAeC

Discussion/Support thread:
  https://retropie.org.uk/forum/topic/13084/freej2me-support-thread

Development thread:
  https://retropie.org.uk/forum/topic/11441/would-you-like-to-play-nokia-j2me-games-on-retropie/

----
**FreeJ2ME Jar Compilation:**

>From the root directory, running the following commands:
>```
> > cd freej2me/
> > ant
>```
> Will create three different jar files inside `build/`:
>
> `freej2me.jar` -> Standalone AWT jar executable
> 
> `freej2me-lr.jar` -> Libretro executable (has to be placed on the frontend's `system/` folder, since it acts as a BIOS for the libretro core and runs J2ME jars)
>
>`freej2me-sdl.jar` -> Jar executable meant to be used in conjunction with SDL2
>
>Both the Libretro and SDL2 jar files need additional binaries to be compiled before use. Look at the additional steps below if you're going to use one of them.

**Building the SDL2 binary:**
>
>To build the SDL2 binary, run the following commands from the root directory:
> ```
> # SDL2 binary compilation
> > cd src/sdl2
> > make
> > make install
> ```
>
> SDL2 allows FreeJ2ME to run on a Raspberry Pi.

**Building the Libretro core (Not working on Windows as of yet):**
>
>To build the libretro core, run the following commands from the root directory:
>```
># libretro core compilation
> > cd src/libretro
> > make
>```
>This will build `freej2me_libretro.so` on `src/libretro/`, which is the core libretro will use to interface with `freej2me-lr.jar`.
>
>Move it to your libretro frontend's `cores/` folder, with freej2me-lr.jar on `system/` and the frontend should be able to load j2me files afterwards.
>
>NOTE: The core DOES NOT WORK on containerized/sandboxed environments unless it can call a java runtime that also resides in the same sandbox or container, keep that in mind if you're running a libretro frontend through something like flatpak or snap for example.

----
**Usage (applies to AWT and SDL):**

Launching the AWT frontend (freej2me.jar) will bring up a filepicker to select the MIDlet to run.

Alternatively it can be launched from the command line: `java -jar freej2me.jar 'file:///path/to/midlet.jar' [width] [height] [scale]`
Where _width_, _height_ (dimensions of the simulated screen) and _scale_ (initial scale factor of the window) are optional arguments.

The SDL2 frontend (freej2me-sdl.jar) accepts the same command-line arguments format, aside from the _scale_ option which is unavailable.

When running under Microsoft Windows please do note paths require an additional `/` prefixed. For example, `C:\path\to\midlet.jar` should be passed as `file:///C:\path\to\midlet.jar`

FreeJ2ME keeps savedata and config at the working directory it is run from. Currently any resolution specified at the config file takes precedence over the values passed via command-line.

---
**How to contribute as a developer:**
  1) Open an Issue
  2) Try solving that issue
  3) Post on the Issue if you have a possible solution
  4) Submit a PR implementing the solution

**If you are not a developer:**
  1) Post on discussion thread only

**Roadmap:**
  - Get as many games as possible to work well.
  - Document games that work well in the wiki
  - Reduce as many bugs as possible
