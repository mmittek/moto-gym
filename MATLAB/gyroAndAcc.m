% http://www.starlino.com/imu_guide.html;
init;

box = makeBox( 0.5,1,2 );
data = loadCSVAndPreprocess('20160927_122606_534.csv');

% Trimming
data = data(2000:end,:);

magnetometer = data(:,[MAGX, MAGY, MAGZ]);
acc = data(:, [ACCX,ACCY,ACCZ]);

accmag = sqrt(sum(acc.^2,2));
m = mean(accmag);
figure(1)

plot( data(:,1), accmag )
hold on;
plot( [ data(1,1) data(end,1) ],[m m ], 'r-x' );
hold off;
xlabel('Time [s]');
ylabel('|ACC|');
grid on;
title('Magnitude of acceleration');

alpha = 0.7;
grav = zeros(1,3);
pos = zeros( size(data,1) ,3);
[gpsx,gpsy,gpsz] = gpsToXYZ(data(:,LAT), data(:,LON), data(:,ALT));


for i=250:size(data,1)
    
    % Extraction and normalization
    grav = alpha.*grav + (1-alpha).*data(i, [ACCX, ACCY, ACCZ]);
    grav = 9.8.*grav./norm(grav);
    accNoDc = data(i,[ACCX,ACCY,ACCZ])-grav;
    pos(i,:) = pos(i-1,:) + 0.5.*accNoDc.*(dt/1000).^2;
    

    
    if(mod(i,10)==0)

        [absOrient] = getAbsoluteOrientation(grav, magnetometer(i,:));

        
        figure(2)
        subplot(1,5,1);
        plot( 0,0 );
        plotVec3D( grav(1) ,grav(3),grav(2));
        axis equal
        axis([-10,10,-10,10,-10,10]);
        grid on;
        title('Gravity from acceleration');
        drawnow;
        
        subplot(1,5,2);
        plot(0,0);
         plotVec3D( data(i,GRAVX) ,data(i,GRAVZ),data(i,GRAVY));
        axis equal
        axis([-10,10,-10,10,-10,10]);
        grid on;
        title('Gravity from readings');
        drawnow;


        subplot(1,5,3);
        plot(0,0);
         plotVec3D( accNoDc(1) ,accNoDc(3),accNoDc(2));
        axis equal
        axis([-10,10,-10,10,-10,10]);
        grid on;
        title('Acceleration without DC');
        drawnow;

        subplot(1,5,4);
        plot(0,0);
        magnVec = magnetometer( i,: )./norm(magnetometer( i,: ));
         plotVec3D( magnVec(1) ,magnVec(3),magnVec(2));
        axis equal
        axis([-1,1,-1,1,-1,1]);
        grid on;
        title('Magnetometer');
        drawnow;

        subplot(1,5,5);
        plot(0,0);
        omega = data(i,[ANGLEX, ANGLEY, ANGLEZ]);
        omegaMag = norm( omega );
        omegan = omega./omegaMag;
         plotVec3D( omegan(1) ,omegan(3),omegan(2) );
        axis equal
        axis([-1,1,-1,1,-1,1]);
        grid on;
        title('Gyro');
        drawnow;


        figure(3)
        subplot(1,2,1);

        plot( data(1:i,1)./1000, pos(1:i,1), 'r-x' );
        hold on;
        plot( data(1:i,1)./1000, pos(1:i,2), 'g-x' );
        plot( data(1:i,1)./1000, pos(1:i,3) , 'b-x');
        hold off;
        grid on;
        xlabel('Time [s]');
        title('Position over time');

        subplot(1,2,2);
        plot( gpsx, gpsy, 'g-' );
        hold on;
        plot( gpsx(i), gpsy(i), 'rO' );
        text( gpsx(i), gpsy(i), sprintf('%.1f', data(i,SPEED)) );
        hold off;
        grid on;
        axis equal;
        title('GPS position');
        drawnow;
    end
end