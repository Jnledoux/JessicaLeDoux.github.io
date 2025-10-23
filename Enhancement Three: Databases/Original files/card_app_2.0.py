import tkinter as tk
from tkinter import ttk
import sqlite3

class SportsCardApp:
    # Paramerters for application
    def __init__(self, master):
        self.master = master
        master.title("Sports Card Collection Manager")
        master.geometry("800x600")

        # Setting up database
        self.conn = sqlite3.connect("sports_cards_2.0.db")

        # Create cursor
        self.cursor = self.conn.cursor()

        # 
        self.create_table()

        # Build the GUI using tk
        self.create_widgets()
        self.view_all_cards()

    def create_table(self):
        # Seeing if a table already exsists 
        # 2.0 addon
        self.cursor.execute("""
            CREATE TABLE IF NOT EXISTS cards (
                id INTEGER PRIMARY KEY,
                player TEXT,
                year INTEGER,
                brand TEXT,
                card_number TEXT,
                value REAL,
                sport TEXT  
            )
        """)

        # Commit the changes
        self.conn.commit()

    def create_widgets(self):
        
        # Creating the different widgets in the aaplication
        # --- Entry Form ---
        form_frame = tk.LabelFrame(self.master, text="Add New Card", padx=10, pady=10)
        form_frame.pack(pady=10, fill="x", padx=10)

        # The different categories you can add for the cards
        # 2.0 addon
        labels = ["Player:", "Year:", "Brand:", "Card Number:", "Value:", "Sport:"]
        self.entries = {}
        for i, text in enumerate(labels):
            tk.Label(form_frame, text=text).grid(row=i, column=0, sticky="w", pady=2)
            entry = tk.Entry(form_frame, width=40)
            entry.grid(row=i, column=1, pady=2, padx=5)
            self.entries[text.replace(":", "")] = entry
        
        # Placeholder for possible card value
        self.entries["Value"].insert(0, "0.00")

        add_button = ttk.Button(form_frame, text="Add Card", command=self.add_card)
        add_button.grid(row=len(labels), column=1, pady=10, sticky="e")

        # --- Card List (Treeview) ---
        list_frame = tk.LabelFrame(self.master, text="Card Collection", padx=10, pady=10)
        list_frame.pack(pady=10, fill="both", expand=True, padx=10)

        columns = ["ID", "Player", "Year", "Brand", "Card Number", "Value"]
        self.card_tree = ttk.Treeview(list_frame, columns=columns, show="headings")
        self.card_tree.pack(fill="both", expand=True)

        for col in columns:
            self.card_tree.heading(col, text=col)
            self.card_tree.column(col, width=100)
        
        # Adding Buttons 
        button_frame = ttk.Frame(self.master)
        button_frame.pack(pady=10)
        
        ttk.Button(button_frame, text="Update Selected", command=self.update_card).pack(side="left", padx=5)
        ttk.Button(button_frame, text="Delete Selected", command=self.delete_card).pack(side="left", padx=5)
        ttk.Button(button_frame, text="Clear Form", command=self.clear_entries).pack(side="left", padx=5)

        self.card_tree.bind("<Double-1>", self.populate_form)

    def add_card(self):
        # Adds the card to the database
        try:
            player = self.entries["Player"].get()
            year = int(self.entries["Year"].get())
            brand = self.entries["Brand"].get()
            card_number = self.entries["Card Number"].get()
            value = float(self.entries["Value"].get())
            # 2.0 addon
            sport = self.entries["Sport"].get()

			# 2.0 addon
            self.cursor.execute("INSERT INTO cards VALUES (NULL, ?, ?, ?, ?, ?, ?)", 
                               (player, year, brand, card_number, value, sport))
            self.conn.commit()
            self.view_all_cards()
            self.clear_entries()
        except (ValueError, sqlite3.Error) as e:
            tk.messagebox.showerror("Error", f"Could not add card. Please check your inputs.\nError: {e}")

    def update_card(self):
        # Updates card in database
        selected_item = self.card_tree.focus()
        if not selected_item:
            tk.messagebox.showwarning("Warning", "Please select a card to update.")
            return
        
        card_id = self.card_tree.item(selected_item, "values")[0]
        try:
            player = self.entries["Player"].get()
            year = int(self.entries["Year"].get())
            brand = self.entries["Brand"].get()
            card_number = self.entries["Card Number"].get()
            value = float(self.entries["Value"].get())
            # 2.0 addon
            sport = self.entries["Sport"].get()

            # 2.0 addon
            self.cursor.execute("""
                UPDATE cards SET player=?, year=?, brand=?, card_number=?, value=?, sport=?, WHERE id=?
            """, (player, year, brand, card_number, value, sport, card_id))
            self.conn.commit()
            self.view_all_cards()
            self.clear_entries()
        except (ValueError, sqlite3.Error) as e:
            tk.messagebox.showerror("Error", f"Could not update card. Please check your inputs.\nError: {e}")

    def delete_card(self):
        # Deletes card in database
        selected_item = self.card_tree.focus()
        if not selected_item:
            tk.messagebox.showwarning("Warning", "Please select a card to delete.")
            return

        card_id = self.card_tree.item(selected_item, "values")[0]
        if tk.messagebox.askyesno("Confirm Deletion", "Are you sure you want to delete this card?"):
            self.cursor.execute("DELETE FROM cards WHERE id=?", (card_id,))
            self.conn.commit()
            self.view_all_cards()
            self.clear_entries()

    def view_all_cards(self):
        # Views all cards in database
        for item in self.card_tree.get_children():
            self.card_tree.delete(item)

        self.cursor.execute("SELECT * FROM cards ORDER BY player")
        rows = self.cursor.fetchall()
        for row in rows:
            self.card_tree.insert("", "end", values=row)

    def populate_form(self, event):
        # Fills the fields to then allow updates
        selected_item = self.card_tree.focus()
        if not selected_item:
            return
            
        values = self.card_tree.item(selected_item, "values")
        if values:
            self.clear_entries()
            self.entries["Player"].insert(0, values[1])
            self.entries["Year"].insert(0, values[2])
            self.entries["Brand"].insert(0, values[3])
            self.entries["Card Number"].insert(0, values[4])
            self.entries["Value"].insert(0, values[5])
            self.entries["Sport"].insert(0,values[6])

    def clear_entries(self):
        # Clears fields
        for entry in self.entries.values():
            entry.delete(0, tk.END)

if __name__ == "__main__":
    root = tk.Tk()
    app = SportsCardApp(root)
    root.mainloop()
