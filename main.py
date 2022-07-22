import json
import tkinter
from turtle import title
import my_scrollbar
import my_entry
import subprocess
import getpass as awesomegt

root = tkinter.Tk()
root.title("Client")

obj = my_scrollbar.ScrollableFrame(
    root,
    height=500, # Total required height of canvas
    width=500 # Total width of master
)

objframe = obj.frame
# use objframe as the main window to make widget

# Global Variables
##########################################################################
my_entry_obj_list = []
serial_no = 1 # Not 0 becoz we have radio button at the row=0
operation = 1 # If opertion is 1 then it means upload and 2 means download
final_json = ''
###########################################################################

def show():
    my_dictionary = {}
    works = []
    for my_entry_obj in my_entry_obj_list:
        # print(my_entry.MyEntry.my_entry_count())
        works.append(my_entry_obj.show())

    global operation
    my_dictionary["operation"] = str(operation)
    # my_dictionary["username"] = "user0"
    my_dictionary["username"] = awesomegt.getuser()

    if operation == 1:
        # Upload
        my_dictionary["works"] = works
    else:
        # files_list = []
        # for work in works:
        #     if len(work["files"]) != 0:
        #         files_list = files_list + work["files"]
        # # operation is 2 meaning Download
        # my_dictionary["files"] = list(set(files_list)) # Removing the duplicate items

        files_name_list = []
        for work in works:
            if len(work["to_users"]) != 0:
                files_name_list = files_name_list + work["to_users"]
        # operation is 2 meaning Download
        my_dictionary["files"] = list(set(files_name_list))

    # print(f"-------------------------{operation}-------------------------------")
    global final_json
    final_json = json.dumps(my_dictionary,indent=4)

    new = tkinter.Toplevel(root)
    newObj = my_scrollbar.ScrollableFrame(new, height=500, # Total required height of canvas
                                                width=500 # Total width of master
                                            )

    newObjFrame = newObj.frame
    #Create a Text in New window
    text_box = tkinter.Text(newObjFrame,height=200,width=150,font=('Arial', 15))
    text_box.grid(row=0,column=0,padx=10,pady=10)
    text_box.insert('end',final_json)
    text_box.config(state=tkinter.DISABLED)
    
    my_dictionary.clear()
    
        

def addEntry():
    global serial_no
    serial_no = serial_no + 1
    my_entry_obj = my_entry.MyEntry(objframe,"to_users","files",20,5)
    my_entry_obj.addEntry(serial_no)
    my_entry_obj_list.append(my_entry_obj)
    
    old_my_button1,old_my_button2, old_my_button3 = update()
    old_my_button1.destroy()
    old_my_button2.destroy()
    old_my_button3.destroy()

def run():
    out_file = open("clientmain.json","w")
    out_file.write(final_json)
    out_file.close()

    #list_of_files = subprocess.run(['ls', '-la'], capture_output=True, text=True)
    command = "javac -cp '.:jar_files/client/*' Client.java && java -cp '.:jar_files/client/*' Client clientmain.json"
    #command2 = "cat clientmain.json"
    client_run = subprocess.run(command,shell=True, capture_output=True, text=True)
    newline = '\n'
    client_run_output = f"STDOUT: {newline}{client_run.stdout}{newline}----------------------------------------------------------------------------{newline}STDERR: {newline}{client_run.stderr}{newline}"
    
    new = tkinter.Toplevel(root)
    newObj = my_scrollbar.ScrollableFrame(new, height=500, # Total required height of canvas
                                                width=500 # Total width of master
                                            )
    newObjFrame = newObj.frame
    text_box = tkinter.Text(newObjFrame,height=200,width=150,font=('Arial', 15))
    text_box.grid(row=0,column=0,padx=10,pady=10)
    text_box.insert('end',client_run_output)


def update():
    global serial_no
    my_button1 = tkinter.Button(objframe,text="Save Changes and See",command=show)
    my_button1.grid(row=serial_no+6,column=0,pady=20)

    my_button2 = tkinter.Button(objframe,text="Add New Entry",command=addEntry)
    my_button2.grid(row=serial_no+7,column=0,pady=20)

    my_button3 = tkinter.Button(objframe,text="Everything is OKAY now RUN",command=run)
    my_button3.grid(row=serial_no+8,column=0,pady=20)

    return my_button1,my_button2, my_button3
   

def setOperation():
    global operation
    operation = intvar.get()
        
intvar = tkinter.IntVar()
tkinter.Radiobutton(objframe, text="UPLOAD [Default]", variable=intvar, value=1,command=setOperation).grid(row=0,column=0,padx=5,pady=5)
tkinter.Radiobutton(objframe, text="DOWNLOAD", variable=intvar, value=2,command=setOperation).grid(row=0,column=1,padx=5,pady=5)

update()


root.mainloop()