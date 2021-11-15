insert into customer.profile (firstName, lastName, socialId, socialIdType) values ('John', 'Doe', '685912-26-5648', 'IC');
insert into customer.account (accountNo, accountType, custId, dailyLimit) values ('1-987654-1234-4569', 'CASA', (select custId from customer.profile), 2500.00);
insert into customer.address (address1, address2, postcode, city, state, country, type, custId) values ('No. 16, Lorong Utama 12', 'Taman Ayer Keroh Heights', '75450', 'Ayer Keroh', 'Melaka', 'MY', 'MAILING', (select custId from customer.profile));

insert into customer.profile (firstName, lastName, socialId, socialIdType) values ('Jenny', 'Joe', '154869-03-2587', 'IC');
insert into customer.account (accountNo, accountType, custId, dailyLimit) values ('1-234567-4321-9876', 'CASA', (select custId from customer.profile where custid = (select max(custid) from customer.profile)), 10000.00);
insert into customer.address (address1, address2, postcode, city, state, country, type, custId) values ('C-20, Lorong Chemor', 'Taman Desa Green', '11600', 'George Town', 'Penang', 'MY', 'MAILING', (select custId from customer.profile where custid = (select max(custid) from customer.profile)));