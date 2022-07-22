from tkinter import *
from tkinter import ttk, filedialog,messagebox

class MyEntry:
    # __my_entry_no = 0

    def __init__(self,master,entry1_text,entry2_text,padx,pady):
        self.master = master
        self.entry1_text = entry1_text
        self.entry2_text = entry2_text
        self.padx = padx
        self.pady = pady

        self.__to_users_list = []
        self.__files = []

        # MyEntry.__my_entry_no = MyEntry.__my_entry_no + 1

    def show(self):
        entry_list_str = str(self.__my_entry1.get())
        if entry_list_str:
            # To remove all whitespace characters (space, tab, newline, and so on)
            entry_list_str = ''.join(entry_list_str.split()) # Here we are using split not strip
            # If you want to only remove whitespace from the beginning and end you can use strip: sentence = sentence.strip()
            self.__to_users_list = entry_list_str.split(',')
            # self.showing_label.config(text=entry_list_str)
        # print(f"-----------------------{MyEntry.__my_entry_no}----------------------------------")
        # print("to_users ",self.__to_users_list)
        # print("files ",self.__files)
        # print("---------------------------------------------------------")

        return {
            "to_users":self.__to_users_list,
            "files":self.__files
        }

    def addEntry(self,serial_no):
    
        to_user_label1 = Label(self.master,text=self.entry1_text)
        to_user_label1.grid(row=serial_no,column=1,pady=self.pady,padx=self.padx)
        self.__my_entry1 = Entry(self.master,width="50",font=("Verdana",15))
        self.__my_entry1.grid(row=serial_no,column=2,pady=self.pady,padx=self.padx)

        to_user_label2 = Label(self.master,text=self.entry2_text)
        to_user_label2.grid(row=serial_no,column=3,pady=self.pady,padx=self.padx)
        # my_entry2 = Entry(self.master)
        # my_entry2.grid(row=serial_no,column=4,pady=self.pady,padx=self.padx)
        ttk.Button(self.master, text="Browse", command=self.open_files).grid(row=serial_no,column=4,pady=self.pady,padx=self.padx)


    def open_files(self):
        files, msgbox = self.main()
        self.__files = files
        while msgbox =='yes':
            files_2, msgbox = self.main()
            for i in files_2:
                self.__files.append(i)
        
    def main(self):
        files = filedialog.askopenfilenames(parent=self.master,title='Choose files')
        msgbox = messagebox.askquestion('Add files','add extra files',icon = 'warning')
        return list(files), msgbox

    # def my_entry_count():
    #     return MyEntry.__my_entry_no


