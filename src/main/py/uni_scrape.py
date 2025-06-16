from selenium import webdriver
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.options import Options
import pandas as pd
import time
import sys
import json
def scrape_uni(username, password):
    #Innit
    chrome_options = Options()
    chrome_options.add_argument("--headless")
    chrome_options.add_argument("--disable-gpu")
    chrome_options.add_argument("--no-sandbox")
    driver = webdriver.Chrome(options=chrome_options)
    
    # Open your university login page
    driver.get('https://uni.tsu.ge/login')
    time.sleep(3)
    #Send creds
    username_field = driver.find_element(By.XPATH, '/html/body/app-root/div/ng-component/div/div/div/form/div[1]/input') 
    password_field = driver.find_element(By.XPATH, '/html/body/app-root/div/ng-component/div/div/div/form/div[2]/input') 
    username_field.send_keys(username)
    password_field.send_keys(password)
    password_field.send_keys(Keys.RETURN)
    time.sleep(5)
    #Go to schedule
    driver.get('https://uni.tsu.ge/schedule') 
    time.sleep(3)
    # Scrape schedule
    schedule_table = driver.find_element(By.XPATH, '/html/body/app-root/div/audexamschedule/div[2]/div/div[2]/div/table')
    # Extract all rows from the table
    rows = schedule_table.find_elements(By.TAG_NAME, 'tr')
    schedule_data = []
    # Get the headers (if present)
    headers = []
    header_row = rows[0]  # Assuming the first row contains headers
    header_columns = header_row.find_elements(By.TAG_NAME, 'th')
    for header in header_columns:
        headers.append(header.text.strip())  
    # Iterate through rows to extract data
    for row in rows[1:]:  # Skip the header row
        columns = row.find_elements(By.TAG_NAME, 'td')  # Extract columns in the row
        # Create a dictionary for each row, using headers and the corresponding data
        schedule_entry = {}
        
        # Assign data to the respective columns (dynamically)
        for idx, column in enumerate(columns):
            if idx < len(headers):  # Ensure we don't exceed the headers list
                schedule_entry[headers[idx]] = column.text.strip()
        # Handle any missing columns by filling them with None or an empty string
        for idx in range(len(columns), len(headers)):
            schedule_entry[headers[idx]] = None
        
        schedule_data.append(schedule_entry)
    df_schedule = pd.DataFrame(schedule_data)
    for column in df_schedule.columns:
        df_schedule[column] = df_schedule[column].apply(lambda x: '\n'.join(x) if isinstance(x, list) else x)
        
    driver.quit()
    return json.dumps({"data": json.loads(df_schedule.to_json(orient="records", force_ascii=False))}, ensure_ascii=False)

if __name__ == "__main__":
    try:
        username = sys.argv[1]
        password = sys.argv[2]
        # Call the scraping function
        result = scrape_uni(username, password)

        # Print result so Java can read it
        sys.stdout.buffer.write(result.encode('utf-8'))  # Ensure UTF-8 output
    except Exception as e:
        error_response = {
            "error": True,
            "message": str(e)
        }
        sys.stdout.buffer.write(json.dumps(error_response).encode('utf-8'))
        sys.exit(1)
