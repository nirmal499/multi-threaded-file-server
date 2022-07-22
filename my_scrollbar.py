
from tkinter import BOTH, BOTTOM, HORIZONTAL, Frame,Scrollbar,VERTICAL,RIGHT,X,Y,Canvas

class ScrollableFrame:
    """A scrollable tkinter frame that will fill the whole window"""

    def __init__ (self, master, width, height, mousescroll=0):
        self.mousescroll = mousescroll
        self.master = master
        self.height = height
        self.width = width
        self.main_frame = Frame(self.master)
        self.main_frame.pack(fill=BOTH, expand=1)

        #Add a Vertical Scrollbar
        self.scrollbar_v = Scrollbar(self.main_frame, orient=VERTICAL)
        self.scrollbar_v.pack(side=RIGHT, fill=Y)

        #Add a Horizontal Scrollbar
        self.scrollbar_h = Scrollbar(self.main_frame, orient= HORIZONTAL)
        self.scrollbar_h.pack(side=BOTTOM, fill=X)

        self.canvas = Canvas(self.main_frame, yscrollcommand=self.scrollbar_v.set,xscrollcommand=self.scrollbar_h.set)
        self.canvas.pack(expand=True, fill=BOTH)

        self.scrollbar_v.config(command=self.canvas.yview)
        self.scrollbar_h.config(command=self.canvas.xview)


        self.canvas.bind(
            '<Configure>',
            lambda e: self.canvas.configure(scrollregion=self.canvas.bbox("all"))
        )

        self.frame = Frame(self.canvas, width=self.width, height=self.height)
        self.frame.pack(expand=True, fill=BOTH)
        self.canvas.create_window((0,0), window=self.frame, anchor="nw")

        self.frame.bind("<Configure>", self.reset_scrollregion)
        
    
    def reset_scrollregion(self, event):
        self.canvas.configure(scrollregion=self.canvas.bbox("all"))

# Example usage

# obj = ScrollableFrame(
#     root,
#     height=300, # Total required height of canvas
#     width=400 # Total width of master
# )

# objframe = obj.frame
# use objframe as the main window to make widget
