import customtkinter as ctk
from tkinter import messagebox, ttk, filedialog, font, END
import sqlite3
import os 
import platform

# ------------------------ CustomTkinter setup ------------------------

# Modes: system (default), light, dark
ctk.set_appearance_mode("dark")  

# Themes: blue (default), dark-blue, green
ctk.set_default_color_theme("dark-blue")  

# ------------------------ Color & Font Palette ------------------------

# Adding colors in one place to add unity
Primary_color = "#1c78ad"
Secondary_color = "#ffffff"
Background_color = "#2a2d2e"
Accent_color = "#f0a500"

# Adding fonts in one place to add unity
Title_font = ("Segoe UI", 45, "bold")
Lable_font = ("Segoe UI", 14, "bold")
Entry_font = ("Segoe UI", 14)
Button_font = ("Segoe UI", 14, "bold")

# --------------------------- Database setup ---------------------------
conn = sqlite3.connect('SportsCardApp.db')
c = conn.cursor()

def create_table():
    c.execute("""
        CREATE TABLE IF NOT EXISTS cards (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            player_f_name TEXT,
            player_l_name TEXT,
            team TEXT,
            card_number TEXT,
            year INTEGER,
            product TEXT,
            sport TEXT,
            value REAL
        )
    """)
    conn.commit()

# ---------------------- GUI APPLICATION ----------------------
class SportsCardApp(ctk.CTk):
    def __init__(self):
        super().__init__()

        self.title("Sports Card Collection Manager")
        self.geometry("1400x800")
        
        create_table()
        self.setup_layout()
        self.generate_treeview()

    # Setting up the frames for the layout
    def setup_layout(self):
        # A main frame for the entire window to provide padding and balance
        self.main_container = ctk.CTkFrame(self)
        self.main_container.pack(fill="both", expand=True, padx=20, pady=20)

        # Split into two main sections: details/actions and the card collection view
        self.top_frame = ctk.CTkFrame(self.main_container)
        self.top_frame.pack(fill="x", pady=(0, 10))
        
        self.view_frame = ctk.CTkFrame(self.main_container)
        self.view_frame.pack(fill="both", expand=True)

        self.detail_frame = ctk.CTkFrame(self.top_frame)
        self.detail_frame.pack(side="left", padx=(0, 10))
        
        self.actions_frame = ctk.CTkFrame(self.top_frame)
        self.actions_frame.pack(side="left", fill="y", padx=(10, 0))

        self.create_card_details_frame()
        self.create_card_actions_frame()
        self.create_card_view_frame()

    # Creating the card details frame
    def create_card_details_frame(self):
        ctk.CTkLabel(self.detail_frame, text="Add New Card", font=Title_font, text_color=Primary_color).pack(pady=(6, 12))

        self.entry_fields = {}
        fields = ["First Name:", "Last Name:", "Team:", "Card Number:", "Year:", "Product:", "Sport:", "Value:"]
        placeholders = [
            "Enter the player's first name",
            "Enter the player's last name",
            "Enter the team name",
            "Enter the card number",
            "Enter the card's year",
            "Enter the product name",
            "Enter the sport",
            "Enter the card's value"
        ]

        for text, placeholder in zip(fields, placeholders):
            sub_frame = ctk.CTkFrame(self.detail_frame, fg_color=Primary_color, corner_radius=10)
            sub_frame.pack(fill="x", padx=12, pady=6)

            ctk.CTkLabel(sub_frame, text=text, width=125, height=25, anchor="w", font=Lable_font, text_color=Secondary_color).pack(side="left", padx=(8, 0))

            entry = ctk.CTkEntry(sub_frame, placeholder_text=placeholder, width=300, justify="center", font=Entry_font, placeholder_text_color=Secondary_color, corner_radius=8)
            entry.pack(side="left", fill="x", expand=True, padx=(2, 10))
            self.entry_fields[text[:-1]] = entry

            if text == "Value:":
                validate_cmd = (self.register(self.validate_value), '%P')
                entry.configure(validate="key", validatecommand=validate_cmd)

    # Validate entry in value and ensure it is a number
    def validate_value(self, p):
        if p == "" or (p.replace('.', '', 1).isdigit() and p.count('.') <= 1):
            return True
        return False

    # Creating the card action frame
    def create_card_actions_frame(self):
        self.add_button = ctk.CTkButton(self.actions_frame, text="Add Card", command=self.add_card, fg_color=Primary_color, hover_color="#145a8d", font=Button_font, text_color=Secondary_color, height=40, corner_radius=8)
        self.add_button.pack(pady=(10, 5), padx=10, fill="x")

        self.update_button = ctk.CTkButton(self.actions_frame, text="Update Card", command=self.update_card, fg_color=Primary_color, hover_color="#145a8d", font=Button_font, text_color=Secondary_color, height=40, corner_radius=8)
        self.update_button.pack(pady=5, padx=10, fill="x")

        self.delete_button = ctk.CTkButton(self.actions_frame, text="Delete Card", command=self.delete_card, fg_color=Primary_color, hover_color="#145a8d", font=Button_font, text_color=Secondary_color, height=40, corner_radius=8)
        self.delete_button.pack(pady=5, padx=10, fill="x")

        self.clear_button = ctk.CTkButton(self.actions_frame, text="Clear Fields", command=self.clear_entries, fg_color=Primary_color, hover_color="#145a8d", font=Button_font, text_color=Secondary_color, height=40, corner_radius=8)
        self.clear_button.pack(pady=5, padx=10, fill="x")

        self.mode_switch = ctk.CTkSwitch(self.actions_frame, text="Dark Mode", command=self.toggle_mode)
        self.mode_switch.pack(pady=10)

    # Adding the option for dark mode with toggle
    def toggle_mode(self):
        current = ctk.get_appearance_mode()
        new_mode = "light" if current == "dark" else "dark"
        ctk.set_appearance_mode(new_mode)    

    # Creating the card view frame to view the database 
    def create_card_view_frame(self):
        ctk.CTkLabel(self.view_frame, text="The Collection", font=Title_font, text_color=Primary_color).pack(pady=(10, 0))

        # The container frame to help with the resizing
        tree_cont_frame = ctk.CTkFrame(self.view_frame)
        tree_cont_frame.pack(fill="both", expand=True, padx=10, pady=10)

        # Using a grid to properly place the treeview 
        self.tree = ttk.Treeview(tree_cont_frame, columns=("ID", "Player F Name", "Player L Name", "Team", "Card Number", "Year", "Product", "Sport", "Value"), show="headings")
        self.tree.grid(row=0, column=0, sticky="nsew")

        # Configuring the grid to help with resizing
        tree_cont_frame.grid_columnconfigure(0, weight=1)
        tree_cont_frame.grid_rowconfigure(0, weight=1)

        # Change the heading names and hide the ID column
        heading_names = ["First Name", "Last Name", "Team", "Card #", "Year", "Product", "Sport", "Value"]
        for i, col in enumerate(self.tree["columns"][1:]):
            self.tree.heading(col, text=heading_names[i], command=lambda _col=col: self.sort_treeview(_col, False))
            self.tree.column(col, width=100, stretch=True)

        self.tree.configure(displaycolumns=("Player F Name", "Player L Name", "Team", "Card Number", "Year", "Product", "Sport", "Value"))
        self.tree.bind("<ButtonRelease-1>", self.on_tree_select)

        # Theme for the headings and treeview windows
        style = ttk.Style()
        style.theme_use("default")
        style.configure("Treeview.Heading", background="#212325", foreground="white", font=Lable_font)
        style.configure("Treeview", background=Background_color, foreground="white", fieldbackground=Background_color, rowheight=25, font=Entry_font)
        style.map('Treeview', background=[('selected', '#5d5d5d')])


    # GET and ADD the details to the database
    def add_card(self):
        player_f_name = self.entry_fields["First Name"].get()
        player_l_name = self.entry_fields["Last Name"].get()
        team = self.entry_fields["Team"].get()
        card_number = self.entry_fields["Card Number"].get()
        year_str = self.entry_fields["Year"].get()
        product = self.entry_fields["Product"].get()
        sport = self.entry_fields["Sport"].get()
        value_str = self.entry_fields["Value"].get()
        
        # Make sure all boxes are filled in
        if not all([player_f_name, player_l_name, team, card_number, year_str, product, sport, value_str]): 
            messagebox.showerror("Error", "All fields must be filled.")
            return

        # Make sure year and value are numbers
        try:
            year = int(year_str)
            value = float(value_str)
        except ValueError:
            messagebox.showerror("Error", "Year must be a valid integer and Value must be a valid number.")
            return

        card_data = (player_f_name, player_l_name, team, card_number, year, product, sport, value)
        c.execute("INSERT INTO cards (player_f_name, player_l_name, team, card_number, year, product, sport, value) VALUES (?, ?, ?, ?, ?, ?, ?, ?)", card_data)
        conn.commit()
        messagebox.showinfo("Success", "Card added successfully.")
        self.clear_entries()
        self.generate_treeview()

    # UPDATE card details to the database 
    def update_card(self):
        selected_item = self.tree.focus()
        if not selected_item:
            messagebox.showerror("Error", "No card selected for update.")
            return
        
        # Get ID from the first item
        card_id = self.tree.item(selected_item, 'values')[0]

        player_f_name = self.entry_fields["First Name:"].get(),
        player_l_name = self.entry_fields["Last Name:"].get(),
        team = self.entry_fields["Team:"].get(),
        card_number = self.entry_fields["Card Number:"].get(),
        year = self.entry_fields["Year:"].get(),
        product = self.entry_fields["Product:"].get(),
        sport = self.entry_fields["Sport:"].get()
        value = self.entry_fields["Value:"].get()
        
        # Make sure all boxes are filled in         
        if not all([player_f_name, player_l_name, team, card_number, year, product, sport, value]): 
            messagebox.showerror("Error", "All fields must be filled.")
            return

        # Make sure value is a number
        try:
            year = int(year_str)
            value = float(value_str)
        except ValueError:
            messagebox.showerror("Error", "Year must be a valid integer and Value must be a valid number.")
            return

        card_data = (player_f_name, player_l_name, team, card_number, year, product, sport, value, card_id)
        c.execute("UPDATE cards SET player_f_name=?, player_l_name=?, team=?, card_number=?, year=?, product=?, sport=?, value=? WHERE id=?", (*values, value, card_id[0]))
        conn.commit()
        messagebox.showinfo("Success", "Card updated successfully.")
        self.clear_entries()
        self.generate_treeview()

    # DELETE card details to the database 
    def delete_card(self):
        selected_item = self.tree.focus()
        if not selected_item:
            messagebox.showerror("Error", "No card selected for deletion.")
            return

        card_id = self.tree.item(selected_item, 'values')[0]
        if messagebox.askyesno("Confirm Deletion", "Are you sure you want to delete this card?"):
            c.execute("DELETE FROM cards WHERE id=?", (card_id[0],))
            conn.commit()
            messagebox.showinfo("Success", "Card deleted successfully.")
            self.clear_entries()
            self.generate_treeview()

    # Generate the tree view of items in the database
    def generate_treeview(self):
        for item in self.tree.get_children():
            self.tree.delete(item)

        c.execute("SELECT id, player_f_name, player_l_name, team, card_number, year, product, sport, value FROM cards")
        rows = c.fetchall()
        for i, row in enumerate(rows):
            tag = 'even' if i % 2 == 0 else 'odd'
            self.tree.insert("", END, values=row, tags=(tag,))
        self.tree.tag_configure('even', background=Background_color)
        self.tree.tag_configure('odd', background="#1f2122")

    # Clear entries when the button is pushed
    def clear_entries(self):
        for entry in self.entry_fields.values():
            entry.delete(0, END)

    def on_tree_select(self, event):
        try:
            selected_item = self.tree.focus()
            if selected_item:
                values = self.tree.item(selected_item, 'values')
                if values:
                    self.clear_entries()
                    keys = ["First Name", "Last Name", "Team", "Card Number", "Year", "Product", "Sport", "Value"]

                    for i, key in enumerate(keys):
                        self.entry_fields[key].insert(0, values[i+1])
        except Exception as e:
            messagebox.showerror("Selection Error", f"Failed to select item: {e}")

    def sort_treeview(self, col, reverse):
        data = [(self.tree.set(item, col), item) for item in self.tree.get_children("")]
        
        try:
            data.sort(key=lambda t: float(t) if col == "Value" and isinstance(t, str) and t.replace('.', '', 1).isdigit() else t, reverse=reverse)
        except (ValueError, IndexError):
            data.sort(key=lambda t: t, reverse=reverse)
        
        for index, (val, item) in enumerate(data):
            self.tree.move(item, "", index)

        self.tree.heading(col, command=lambda: self.sort_treeview(col, not reverse))

if __name__ == "__main__":
    app = SportsCardApp()
    app.mainloop()
    conn.close()

