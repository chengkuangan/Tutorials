psql -U camel -d dbzdemo -c "INSERT INTO braindose.orders (orderid, orderdate, sku, description, amount) values (1, '2022/11/25', 'Apple Pen 2', 'Apple Pen Generation 2', 199.56)" && \
psql -U camel -d dbzdemo -c "INSERT INTO braindose.customerOrders (custId, orderid) values (1, 1)"

psql -U camel -d dbzdemo -c "INSERT INTO braindose.orders (orderid, orderdate, sku, description, amount) values (2, '2022/11/25', 'Apple Magic Keyboard', 'Apple Magic Keyboard for iPad Pro 11.9\"', 300.56)"  && \
psql -U camel -d dbzdemo -c "INSERT INTO braindose.customerOrders (custId, orderid) values (1, 2)"

psql -U camel -d dbzdemo -c "INSERT INTO braindose.orders (orderid, orderdate, sku, description, amount) values (3, '2022/11/26', 'Apple Magic Keyboard', 'Apple Magic Keyboard for iPad Air 4th Gen\"', 250.56)"  && \
psql -U camel -d dbzdemo -c "INSERT INTO braindose.customerOrders (custId, orderid) values (2, 3)"

psql -U camel -d dbzdemo -c "INSERT INTO braindose.orders (orderid, orderdate, sku, description, amount) values (4, '2022/11/27', 'Apple iPad', 'Apple iPad 5th Generation\"', 150.56)"  && \
psql -U camel -d dbzdemo -c "INSERT INTO braindose.customerOrders (custId, orderid) values (1, 4)"

psql -U camel -d dbzdemo -c "INSERT INTO braindose.orders (orderid, orderdate, sku, description, amount) values (5, '2022/11/28', 'Apple Macbook Pro', 'Apple Macbook Pro 16\"', 6500.00)"  && \
psql -U camel -d dbzdemo -c "INSERT INTO braindose.customerOrders (custId, orderid) values (2, 5)"
