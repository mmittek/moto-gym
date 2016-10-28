init;
if(~exist('dataLoaded'))
    dataLoaded = 1;
    data = loadCSVAndPreprocess('../Data/front_back.csv');
end

%data = data(250:end,:);
data(:,1) = data(:,1) - data(1,1);

idx = find( data(:,GPSACC) < 13 );
[x,y] = gpsToXYZ(data(:,LAT), data(:,LON), data(:,ALT));
x = x-x(1);
y = y-y(1);

figure(1)
plot(x(idx),y(idx), 'r-o');
axis equal;
grid on;

figure(2)
subplot(3,1,1);
plot( data(:,1), data(:,ACCX) );
hold on;
plot( data(:,1), data(:,ACCY) );
plot( data(:,1), data(:,ACCZ) );
hold off;
grid on;
subplot(3,1,2);
plot( data(:,1), data(:,GRAVX) );
hold on;
plot( data(:,1), data(:,GRAVY) );
plot( data(:,1), data(:,GRAVZ) );
hold off;
grid on;
subplot(3,1,3);
plot( data(:,1), data(:,ACCY)-data(:,GRAVX) );
hold on;
plot( data(:,1), data(:,ACCY)-data(:,GRAVY) );
plot( data(:,1), data(:,ACCZ)-data(:,GRAVZ) );
hold off;
grid on;


%grav = recursiveLPF( data(:,[ACCX,ACCY,ACCZ]), 0.9 );
grav = data(:,[GRAVX,GRAVY,GRAVZ]);
grav = grav - mean(grav);

box4d = makeBox(0.5,1,4);

for i=1:1:size(data,1)
    R = R3DtoR4D( reshape(data(i,ROTMAT)',[3,3]) );
    box4dr = (R*box4d')';
    
    figure(3)
    plot(0,0);
    plotVec3D( grav(i,1) , grav(i,3),grav(i,2));
    
    hold on;
    plot3( box4dr(:,1), box4dr(:,3), box4dr(:,2), 'b-o'  );
    hold off;
    
    grid on;
    axis equal;
    axis([-10,10,-10,10,-10,10]);
    view(3);
    title(sprintf('Time: %d', data(i,1)/1000 ));
    drawnow;
end