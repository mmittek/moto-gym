data = csvread('2016-09-25 22_27_17_260.csv',1);

data = sortrows(data,1);
data = data(500:end,:);

speed= data(:,end-2);
gps = data(:,end-1:end);

angles = gps ./ repmat( pi/180, [size(gps,1),2] );

r = 6371 + 138;
x = r.*cos( angles(:,1) ).*cos( angles(:,2) );
y = r.*cos( angles(:,1) ).*sin( angles(:,2) );
z = r.*sin( angles(:,1) );

figure(1)
clf;
plot( x,y, 'r--.' );
axis equal;


figure(2)
clf;
plot( data(:,1), speed *3.6 );


