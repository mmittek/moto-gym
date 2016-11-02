init;
if ~exist('dataLoaded')
    csvFilename = '../Data/20161002_115147_542_90.csv';
    data = loadCSVAndPreprocess(csvFilename);
   % data = data(2204:end,:);
    data(:,1) = data(:,1) - data(1,1);
    dataLoaded = 1;
end


mag = data(:, [MAGX, MAGY, MAGZ]);


% [x,y,z] = gpsToXYZ( data(:,[LAT]),data(:,[LON]), data(:,ALT)  );

figure(2); clf;
figure(1); clf;
fw = 50;

acc = data(:,[ACCX,ACCY,ACCZ]);
%[t vq dt] = sampleUniformly(data(:,1), acc);
%grav = filter( ones(1,fw)./fw,1,vq);

alpha = 0.99;

gravDelay = 25;



angle = zeros(1,3);
box = makeBox(0.5,1,4);
grav = zeros(1,3);
for i=2:size(data,1)
    dt = (data(i,1)-data(i-1,1));    % go to seconds
    angle = angle + data(i,[GYROX, GYROY, GYROZ])*dt/1000;
    angleDeg = angle*180/pi;
    
    grav = grav.*alpha + (1-alpha).*acc(i,:);
    
    % Get indices for gravity average
%    idx = find( data(:,1) >= data(i,1)-100);
%    idx = find(idx<i);   
%    grav = sum((data(idx,[ACCX,ACCY,ACCZ]).*repmat(dt,[numel(idx),3])))./(dt*numel(idx));
%    grav = mean( data(max(1,i-100), [ACCX,ACCY,ACCZ]),1 );

    
    if(mod(i,10)==0)
        R = rotXYZ( angle(1), angle(2), angle(3) );
        boxr = (R*box')';

        
        figure(1)
         subplot(1,2,1);
        plot3( boxr(:,1), boxr(:,2), boxr(:,3), 'r-o' );
        hold on;
%        plotVec3D( mag(i,1)/norm(mag(i,:)),mag(i,3)/norm(mag(i,:)), mag(i,2)/norm(mag(i,:)));
        plotVec3D( grav(1), grav(2), grav(3) );
        hold off;
        axis equal;
        axis([-1,1,-1,1,-1,1]);
        grid on;
        title(sprintf('Frame %d/%d, angle: %.2f, %.2f, %.2f', i, size(data,1), angleDeg(1),angleDeg(2),angleDeg(3) ));

    %     subplot(1,2,2);
    %     plot( x(1:i), y(1:i), 'r-x' );
    %     axis equal;
    %     axis( [x(i)-0.5,x(i)+0.5,y(i)-0.5, y(i)+0.5] );
    %     grid on;


        subplot(1,2,2);
        plot(data(:,1), data(:, ACCX), 'r-' );
        hold on;
        plot( data(:,1), data(:, ACCY), 'g-' );
        plot( data(:,1),data(:, ACCZ), 'b-' );
        plot( [data(i,1),data(i,1)],[-10,10],'r-' );

        axis( [1,data(end,1),-10,10] );
        hold off;
        grid on;
        drawnow;
    end

end




