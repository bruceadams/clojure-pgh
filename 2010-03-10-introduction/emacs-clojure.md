Emacs/Clojure Notes
===================

What?
-----

*  *Emacs*

   > "The extensible, customizable, self-documenting, real-time display 
   > editor."

   Installed using the normal package manager for your operating
   system, and configured over long-fought battles at the keyboard.

*  *SLIME & Swank*

   > "The superior lisp interaction mode for Emacs."

   Installed using ELPA (the Emacs Lisp Package Archive).  SLIME is
   code that runs in Emacs, giving it an understanding of how to work
   with Lisp code.  It communicates over a socket with Swank, a server
   that invokes the Lisp environment of your choice.  This allows you
   to execute code in Emacs that is then sent to your program running
   persistently in a separate REPL.  When you install the Swank
   implementation for Clojure (swank-clojure) it will automatically
   download and install clojure in a hidden file in your home directory.

   Execute `M-x slime` from within Emacs to start SLIME (and Swank).

*  *Clojure & REPL*

   In this case, Clojure's Read-Eval-Print-Loop (REPL) is run from
   within Emacs by Swank on your behalf.


Why?
----

It's the best of both worlds, and then some.

By using SLIME and Swank, rather than just an ordinary REPL or `clj
somefile.clj`, you can interact with your program as it is running, by
sending commands to it from Emacs (much like the normal REPL).

However, you are also able to edit your code using all the normal
power of emacs, and then send the changes into the REPL, without
restarting your program.

This gives you a hyrbid environment where you can write exploratory
test code and develop your application as you go, but also interact
with your program as you write it, never needing to restart it from
scratch.

This also means you only need to have Emacs open, and nothing else.

How?
----

1. Install clojure-mode, slime, slime-repl, and swank-clojure using
   ELPA. Whew!
2. Run emacs and visit the file you want to work with.
3. Execute `M-x slime` to start SLIME.
4. Spend 10 years learning how to work with Emacs.

Useful Emacs Commands
---------------------

### Ordinary Key Bindings
*  `C-x C-c` -- quit emacs
*  `C-x C-s` -- save file
*  `C-x C-f` -- open a file
*  `C-x C-b` -- switch between buffers
*  `C-<space>` -- set mark (begin highlighting)
*  `C-w` -- cut ("kill") selected text
*  `M-w` -- copy selected text
*  `M-h` -- select the current paragraph\*
*  `C-x o` -- switch to other window
*  `C-x 0` -- close this window
*  `C-x 1` -- close other windows
*  `C-g` -- quit (stop it!)

### Special SLIME Key Bindings
*  `M-TAB` -- autocomplete expression
*  `C-x C-e` -- eval the form under the point
*  `C-c C-k` -- compile the current buffer
*  `C-c C-m` -- macroexpand
*  `C-c C-d C-d` -- display documentation
*  `C-c C-z` -- switch to repl buffer

### So Many More
* [http://www.gnu.org/software/emacs/](http://www.gnu.org/software/emacs/)
* [http://www.math.uh.edu/~bgb/emacs_keys.html](http://www.math.uh.edu/~bgb/emacs_keys.html)
* [http://github.com/jochu/swank-clojure](http://github.com/jochu/swank-clojure)

